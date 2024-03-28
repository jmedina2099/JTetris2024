import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Socket } from 'socket.io-client';
import { Box } from 'src/app/model/figure/box';
import { Figure } from 'src/app/model/figure/figure';
import { FetchService } from 'src/app/services/fetch/fetch.service';
import { WebSocketService } from 'src/app/services/socket-io/web-socket.service';

@Component({
  selector: 'app-ventana-principal',
  templateUrl: './ventana-principal.component.html',
  styleUrls: ['./ventana-principal.component.css'],
})
export class VentanaPrincipalComponent implements OnInit {
  socket: Socket | undefined;

  constructor(
    private fetchService: FetchService,
    private webSocketService: WebSocketService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initSocket();
  }

  getFallingBoxes() {
    return this.route.snapshot.data['fallingBoxes'];
  }

  getBoard() {
    return this.route.snapshot.data['board'];
  }

  initSocket(): void {
    this.socket = this.webSocketService.getSocket();
    if (this.socket) {
      this.socket.on('nextFigureMessage', (data: string) => {
        //console.log('on nextFigureMessage = ' + data);
        const figure: Figure = JSON.parse(data) as Figure;
        const initialTimeStamp: number = this.route.snapshot.data[
          'initialTimeStamp'
        ] as number;
        if (initialTimeStamp < figure.initialTimeStamp) {
          //console.log( '--> nextFigureMessage (kafka) = '+figure.timeStamp );
          this.route.snapshot.data['initialTimeStamp'] =
            figure.initialTimeStamp;
          this.route.snapshot.data['timeStamp'] = figure.timeStamp;
          this.route.snapshot.data['fallingBoxes'] = figure.boxes;
        }
      });
      this.socket.on('fallingFigureMessage', (data: string) => {
        //console.log('on fallingFigureMessage = ' + data);
        const figure: Figure = JSON.parse(data) as Figure;
        const initialTimeStamp: number = this.route.snapshot.data[
          'initialTimeStamp'
        ] as number;
        const currentTimeStamp: number = this.route.snapshot.data[
          'timeStamp'
        ] as number;
        if (
          initialTimeStamp <= figure.initialTimeStamp &&
          currentTimeStamp < figure.timeStamp
        ) {
          //console.log( '--> fallingFigureMessage (kafka) = '+figure.timeStamp );
          this.route.snapshot.data['timeStamp'] = figure.timeStamp;
          this.route.snapshot.data['fallingBoxes'] = figure.boxes;
        }
      });
      this.socket.on('boardMessage', (data: string) => {
        //console.log('on boardMessage (kafka) = ' + data);
        const boxes = JSON.parse(data) as Box[] | undefined;
        if (boxes && boxes.length > 0) {
          const doPaint: boolean = boxes.every(
            b => this.route.snapshot.data['board'].timeStamp < b.timeStamp
          );
          if (doPaint) {
            //console.log( '--> boardMessage (kafka) = '+boxes[0].timeStamp );
            this.route.snapshot.data['board'].timeStamp = boxes[0].timeStamp;
            this.route.snapshot.data['board'].boxes = boxes;
          }
        }
      });
      this.socket.on('connect', () => {
        console.log('on connect !!!');
      });
    }
  }

  nextFigure(): void {
    const startButtonElement = window.document.getElementById('startButton');
    if (startButtonElement) {
      startButtonElement.blur();
    }
    this.route.snapshot.data['board'].boxes = [];
    this.route.snapshot.data['board'].timeStamp = 0;
    this.route.snapshot.data['initialTimeStamp'] = 0;
    this.route.snapshot.data['timeStamp'] = 0;
    this.route.snapshot.data['fallingBoxes'] = [];
    const timeStamp: number[] = [0];
    this.fetchService.start().subscribe({
      next: (box: Box) => this.fillNextFigure(box, timeStamp),
      complete: () => this.endFillNextFigure(timeStamp),
    });
  }

  fillNextFigure(box: Box, timeStamp: number[]): void {
    const initialTimeStamp: number = this.route.snapshot.data[
      'initialTimeStamp'
    ] as number;
    if (initialTimeStamp < box.initialTimeStamp) {
      if (timeStamp[0] == 0) {
        this.route.snapshot.data['fallingBoxes'] = [];
        timeStamp[0] = box.timeStamp;
      }
      this.route.snapshot.data['fallingBoxes'].push(box);
    }
  }

  endFillNextFigure(timeStamp: number[]): void {
    if (timeStamp[0] != 0) {
      //console.log( '--> endFillNextFigure (reactive) = '+timeStamp[0] );
      this.route.snapshot.data['initialTimeStamp'] = timeStamp[0];
      this.route.snapshot.data['timeStamp'] = timeStamp[0];
    }
  }
}
