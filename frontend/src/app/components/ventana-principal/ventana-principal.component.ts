import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Socket } from 'socket.io-client';
import { BoardOperation } from 'src/app/model/board/boardOperation';
import { Box } from 'src/app/model/figure/box';
import {
  FigureOperation,
  FigureOperationType,
} from 'src/app/model/figure/figureOperation';
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

  protected getFallingBoxes(): Box[] {
    return this.route.snapshot.data['figureOperation']?.figure.boxes as Box[];
  }

  protected getBoardBoxes(): Box[] {
    return this.route.snapshot.data['boardOperation']?.boxes as Box[];
  }

  private getFigureInitialTimeStamp(): number {
    return this.route.snapshot.data['figureOperation']
      ?.initialTimeStamp as number;
  }

  private getFigureTimeStamp(): number {
    return this.route.snapshot.data['figureOperation']?.timeStamp as number;
  }

  private getBoardTimeStamp(): number {
    return this.route.snapshot.data['boardOperation']?.timeStamp as number;
  }

  private initSocket(): void {
    this.socket = this.webSocketService.getSocket();
    if (this.socket) {
      this.socket.on('nextFigureMessage', (data: string) => {
        //console.log('on nextFigureMessage (kafka)...');
        const op: FigureOperation = JSON.parse(data) as FigureOperation;
        this.validateFigureOperation(op, 2)
          ? this.setFigureOperation(op, 2)
          : false;
      });
      this.socket.on('fallingFigureMessage', (data: string) => {
        //console.log('on fallingFigureMessage (kafka)...');
        const op: FigureOperation = JSON.parse(data) as FigureOperation;
        this.validateFigureOperation(op, 2)
          ? this.setFigureOperation(op, 2)
          : false;
      });
      this.socket.on('boardMessage', (data: string) => {
        //console.log('on boardMessage (kafka)...');
        const op: BoardOperation = JSON.parse(data) as BoardOperation;
        this.validateBoardOperation(op, 2)
          ? this.setBoardOperation(op, 2)
          : false;
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
            next: (op: FigureOperation) =>
              this.validateFigureOperation(op, 1)
                ? this.setFigureOperation(op, 1)
                : false,
          });
          this.fetchService.getBoardConversation().subscribe({
            next: (op: BoardOperation) =>
              this.validateBoardOperation(op, 1)
                ? this.setBoardOperation(op, 1)
                : false,
          });
        }
      },
    });
  }

  private reset(): void {
    this.route.snapshot.data['boardOperation'].boxes = [];
    this.route.snapshot.data['boardOperation'].timeStamp = 0;
    this.route.snapshot.data['boardOperation'].operation = undefined;
    this.route.snapshot.data['figureOperation'].figure = {};
    this.route.snapshot.data['figureOperation'].initialTimeStamp = 0;
    this.route.snapshot.data['figureOperation'].timeStamp = 0;
    this.route.snapshot.data['figureOperation'].operation = undefined;
  }

  private getTube(id: number): string {
    return id == 1 ? 'reactive' : 'kafka';
  }

  private validateFigureOperation(op: FigureOperation, id: number): boolean {
    //console.log('--> validateFigureOperation (%s)...', this.getTube(id));
    const operation: FigureOperationType = op.operation;
    const initialTimeStamp: number = this.getFigureInitialTimeStamp();
    const currentTimeStamp: number = this.getFigureTimeStamp();
    const figInitialTimeStamp: number = op.initialTimeStamp as number;
    const figTimeStamp: number = op.timeStamp as number;
    switch (operation) {
      case 'NEW_OPERATION': {
        if (initialTimeStamp < figInitialTimeStamp) {
          return true;
        }
        break;
      }
      case 'MOVEMENT_OPERATION':
      case 'ROTATION_OPERATION': {
        if (
          initialTimeStamp === figInitialTimeStamp &&
          currentTimeStamp < figTimeStamp
        ) {
          return true;
        }
        break;
      }
      default: {
        console.log('--> Invalid FigureOperation: ' + operation);
        break;
      }
    }
    return false;
  }

  private validateBoardOperation(op: BoardOperation, id: number): boolean {
    //console.log('---> validateAndSetBoardOperation (%s)', this.getTube(id));
    const currentTimeStamp: number = this.getBoardTimeStamp();
    const boardTimeStamp: number = op.timeStamp;
    if (currentTimeStamp < boardTimeStamp) {
      return true;
    }
    return false;
  }

  private setFigureOperation(op: FigureOperation, id: number): void {
    //console.log('--> set figure (%s) = (%s)',this.getTube(id),JSON.stringify(op));
    this.route.snapshot.data['figureOperation'] = op;
  }

  private setBoardOperation(op: BoardOperation, id: number): void {
    //console.log('--> set board (%s) = (%s)',this.getTube(id),JSON.stringify(op));
    this.route.snapshot.data['boardOperation'] = op;
    if (this.getFigureTimeStamp() < op.timeStamp) {
      this.route.snapshot.data['figureOperation'].timeStamp = op.timeStamp;
      this.route.snapshot.data['figureOperation'].figure = {};
    }
  }
}
