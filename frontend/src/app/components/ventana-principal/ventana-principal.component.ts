import { Component, OnInit } from '@angular/core';
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
  //fallingFigure: Figure = { boxes: [] };
  fallingBoxes: Box[] = [];
  board: Box[] = [];
  socket: Socket | undefined;

  constructor(
    private fetchService: FetchService,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit(): void {
    this.initSocket();
  }

  initSocket(): void {
    this.socket = this.webSocketService.getSocket();
    if (this.socket) {
      this.socket.on('fallingFigureMessage', (data: string) => {
        console.log('on fallingFigureMessage = ' + data);
        const figure = JSON.parse(data) as Figure;
        this.fallingBoxes = figure.boxes;
      });
      this.socket.on('boardMessage', (data: string) => {
        //console.log('on boardMessage = ' + data);
        this.board = JSON.parse(data) as Box[];
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
    this.fetchService.start().subscribe();
  }
}
