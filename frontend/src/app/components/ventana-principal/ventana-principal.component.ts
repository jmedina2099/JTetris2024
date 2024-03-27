import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Socket } from 'socket.io-client';
import { Board } from 'src/app/model/board/board';
import { Figure } from 'src/app/model/figure/figure';
import { FetchService } from 'src/app/services/fetch/fetch.service';
import { WebSocketService } from 'src/app/services/socket-io/web-socket.service';

@Component({
  selector: 'app-ventana-principal',
  templateUrl: './ventana-principal.component.html',
  styleUrls: ['./ventana-principal.component.css'],
})
export class VentanaPrincipalComponent implements OnInit {
  board: Board = { boxes: [], timeStamp: 0 };
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

  initSocket(): void {
    this.socket = this.webSocketService.getSocket();
    if (this.socket) {
      this.socket.on('nextFigureMessage', (data: string) => {
        //console.log('on nextFigureMessage = ' + data);
        const figure: Figure = JSON.parse(data) as Figure;
        const initialTimeStamp: number = this.route.snapshot.data[
          'initialTimeStamp'
        ] as number;
        if (initialTimeStamp <= figure.initialTimeStamp) {
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
          this.route.snapshot.data['timeStamp'] = figure.timeStamp;
          this.route.snapshot.data['fallingBoxes'] = figure.boxes;
        }
      });
      this.socket.on('boardMessage', (data: string) => {
        //console.log('on boardMessage = ' + data);
        const board = JSON.parse(data) as Board;
        if (this.board.timeStamp < board.timeStamp) {
          this.board = board;
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
    this.board.boxes = [];
    this.board.timeStamp = 0;
    this.route.snapshot.data['initialTimeStamp'] = 0;
    this.route.snapshot.data['timeStamp'] = 0;
    this.route.snapshot.data['fallingBoxes'] = [];
    this.fetchService.start().subscribe();
  }
}
