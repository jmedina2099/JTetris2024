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
  private socket: Socket | undefined;

  constructor(
    private fetchService: FetchService,
    private webSocketService: WebSocketService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initSocket();
  }

  protected getFallingBoxes() {
    return this.route.snapshot.data['figureFalling']?.boxes;
  }

  protected getBoard() {
    return this.route.snapshot.data['board']?.boxes;
  }

  private initSocket(): void {
    this.socket = this.webSocketService.getSocket();
    if (this.socket) {
      this.socket.on('nextFigureMessage', (data: string) => {
        //console.log('on nextFigureMessage (kafka)...');
        const figure: Figure = JSON.parse(data) as Figure;
        const initialTimeStamp: number = this.route.snapshot.data[
          'figureFalling'
        ]?.initialTimeStamp as number;
        if (initialTimeStamp < figure.initialTimeStamp) {
          //console.log( '--> set next figure (kafka) = '+figure.timeStamp );
          this.route.snapshot.data['figureFalling'] = figure;
        }
      });
      this.socket.on('fallingFigureMessage', (data: string) => {
        //console.log('on fallingFigureMessage (kafka)...');
        const figure: Figure = JSON.parse(data) as Figure;
        const initialTimeStamp: number = this.route.snapshot.data[
          'figureFalling'
        ]?.initialTimeStamp as number;
        const currentTimeStamp: number = this.route.snapshot.data[
          'figureFalling'
        ]?.timeStamp as number;
        if (
          initialTimeStamp <= figure.initialTimeStamp &&
          currentTimeStamp < figure.timeStamp
        ) {
          //console.log( '--> set falling figure (kafka) = '+figure.timeStamp );
          this.route.snapshot.data['figureFalling'] = figure;
        }
      });
      this.socket.on('boardMessage', (data: string) => {
        //console.log('on boardMessage (kafka)...');
        const board = JSON.parse(data) as Board | undefined;
        const boardTimeStamp: number = this.route.snapshot.data['board']
          ?.timeStamp as number;
        if (board && board.boxes.length > 0) {
          if (boardTimeStamp < board.timeStamp) {
            //console.log( '--> set board (kafka) = '+board.timeStamp );
            this.route.snapshot.data['board'] = board;
          }
        }
      });
      this.socket.on('connect', () => {
        console.log('on connect !!!');
      });
    }
  }

  protected start(): void {
    const startButtonElement = window.document.getElementById('startButton');
    if (startButtonElement) {
      startButtonElement.blur();
    }
    this.reset();
    this.fetchService.start().subscribe({
      next: (value: boolean) => {
        if (value) {
          this.fetchService.getFigureConversation().subscribe({
            next: (figure: Figure) => this.setFigureOperation(figure),
          });
          this.fetchService.getBoardConversation().subscribe({
            next: (board: Board) => this.setBoardOperation(board),
          });
        }
      },
    });
  }

  private reset(): void {
    this.route.snapshot.data['board'].boxes = [];
    this.route.snapshot.data['board'].timeStamp = 0;
    this.route.snapshot.data['figureFalling'].initialTimeStamp = 0;
    this.route.snapshot.data['figureFalling'].timeStamp = 0;
    this.route.snapshot.data['figureFalling'].boxes = [];
  }

  private setFigureOperation(figure: Figure): void {
    //console.log( '--> setFigureOperation (reactive)...' );
    const initialTimeStamp: number = this.route.snapshot.data['figureFalling']
      ?.initialTimeStamp as number;
    const currentTimeStamp: number = this.route.snapshot.data['figureFalling']
      ?.timeStamp as number;
    if (
      initialTimeStamp <= figure.initialTimeStamp &&
      currentTimeStamp < figure.timeStamp
    ) {
      //console.log( '--> set figure (reactive) = '+figure.timeStamp );
      this.route.snapshot.data['figureFalling'] = figure;
    }
  }

  setBoardOperation(board: Board): void {
    //console.log( '---> setBoardOperation (reactive)...' );
    if (board && board.boxes.length > 0) {
      const currentTimeStamp: number = this.route.snapshot.data['board']
        ?.timeStamp as number;
      if (currentTimeStamp < board.timeStamp) {
        //console.log( '--> set board (reactive) = '+board.timeStamp );
        this.route.snapshot.data['board'] = board;
      }
    }
  }
}
