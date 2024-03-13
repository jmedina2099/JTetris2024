import { Component, OnInit } from '@angular/core';
import { Socket } from 'socket.io-client';
import { Figure } from 'src/app/model/figure/figure';
import { FetchService } from 'src/app/services/fetch/fetch.service';
import { WebSocketService } from 'src/app/services/socket-io/web-socket.service';

@Component({
  selector: 'app-ventana-principal',
  templateUrl: './ventana-principal.component.html',
  styleUrls: ['./ventana-principal.component.css'],
})
export class VentanaPrincipalComponent implements OnInit {
  fallingFigure: Figure = { boxes: [] };
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
      this.socket.on('message', (data: string) => {
        console.log('on message = ' + data);
        this.fallingFigure = JSON.parse(data) as Figure;
      });
      this.socket.on('connect', () => {
        console.log('on connect !!!');
      });
    }
  }

  nextFigure(): void {
    this.fetchService.start().subscribe((value: string) => {
      console.log('start = ' + value);
    });
  }
}
