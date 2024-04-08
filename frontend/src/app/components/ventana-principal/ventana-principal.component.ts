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
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-ventana-principal',
  templateUrl: './ventana-principal.component.html',
  styleUrls: ['./ventana-principal.component.css'],
})
export class VentanaPrincipalComponent implements OnInit {
  private socket: Socket | undefined;
  protected statistics: number[][] = [
    [0, 0],
    [0, 0],
  ];
  protected showStatistics: boolean = environment.showStatistics;
  protected isRunning = false;
  protected startTime = 0;
  protected minutesEllapsed = 0;
  protected setsOfFigure = 0;
  protected setsOfBoard = 0;
  protected secondsEllapsed = 0;

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
        this.validateFigureOperation(op, 1)
          ? this.setFigureOperation(op, 1)
          : false;
      });
      this.socket.on('fallingFigureMessage', (data: string) => {
        //console.log('on fallingFigureMessage (kafka)...');
        const op: FigureOperation = JSON.parse(data) as FigureOperation;
        this.validateFigureOperation(op, 1)
          ? this.setFigureOperation(op, 1)
          : false;
      });
      this.socket.on('boardMessage', (data: string) => {
        //console.log('on boardMessage (kafka)...');
        const op: BoardOperation = JSON.parse(data) as BoardOperation;
        this.validateBoardOperation(op, 1)
          ? this.setBoardOperation(op, 1)
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
        this.isRunning = value;
        this.route.snapshot.data['game'].isRunning = value;
        if (value) {
          this.startTime = Date.now();
          window.setInterval(() => {
            this.secondsEllapsed = this.startTime > 0? Math.floor((Date.now() - this.startTime) / 1000) % 60: 0;
            this.minutesEllapsed = this.startTime > 0? Math.floor((Date.now() - this.startTime) / (1000*60)): 0;
          }, 1000);
          this.fetchService.getFigureConversation().subscribe({
            next: (op: FigureOperation) =>
              this.validateFigureOperation(op, 0)
                ? this.setFigureOperation(op, 0)
                : false,
            //error: err => console.log('--> getFigureConversation.error = '+err),
            //complete: () => console.log('--> getFigureConversation.complete!'),
          });
          this.fetchService.getBoardConversation().subscribe({
            next: (op: BoardOperation) =>
              this.validateBoardOperation(op, 0)
                ? this.setBoardOperation(op, 0)
                : false,
            //error: err => console.log('--> getBoardConversation.error = '+err),
            //complete: () => console.log('--> getBoardConversation.complete!'),
          });
        }
      },
    });
  }

  protected stop(): void {
    this.fetchService.stop().subscribe({
      next: () => {
        this.reset();
        this.isRunning = false;
        this.route.snapshot.data['game'].isRunning = false;
        this.secondsEllapsed = 0;
        this.minutesEllapsed = 0;
        this.startTime = 0;
        this.setsOfFigure = 0;
        this.setsOfBoard = 0;
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
    this.statistics = [
      [0, 0],
      [0, 0],
    ];
  }

  private getTube(id: number): string {
    return id == 0 ? 'reactive' : id == 1 ? 'kafka' : '';
  }

  private validateFigureOperation(op: FigureOperation, id: number): boolean {
    //console.log('--> validateFigureOperation (%s) = (%s)', this.getTube(id),JSON.stringify(op));
    const operation: FigureOperationType = op.operation;
    const initialTimeStamp: number = this.getFigureInitialTimeStamp();
    const currentTimeStamp: number = this.getFigureTimeStamp();
    const figInitialTimeStamp: number = op.initialTimeStamp as number;
    const figTimeStamp: number = op.timeStamp as number;
    switch (operation) {
      case 'NEW_OPERATION': {
        if (initialTimeStamp < figInitialTimeStamp) {
          this.route.snapshot.data['waitForBottomDown'].waiting = false;
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
        console.log('--> Invalid FigureOperation: %s %d', operation, id);
        break;
      }
    }
    return false;
  }

  private validateBoardOperation(op: BoardOperation, id: number): boolean {
    //console.log('---> validateAndSetBoardOperation (%s) = (%s)', this.getTube(id),JSON.stringify(op));
    const currentTimeStamp: number = this.getBoardTimeStamp();
    const boardTimeStamp: number = op.timeStamp;
    if (currentTimeStamp < boardTimeStamp) {
      return true || id;
    }
    return false;
  }

  private setFigureOperation(op: FigureOperation, id: number): void {
    this.statistics[id][0]++;
    //console.log('--> set figure (%s)-(%d) = (%s)',this.getTube(id),this.statistics[id][0],JSON.stringify(op));
    this.route.snapshot.data['figureOperation'] = op;
    this.setsOfFigure++;
  }

  private setBoardOperation(op: BoardOperation, id: number): void {
    this.statistics[id][1]++;
    //console.log('--> set board (%s)-(%d) = (%s)',this.getTube(id),this.statistics[id][1],JSON.stringify(op));
    this.route.snapshot.data['boardOperation'] = op;
    if (this.getFigureTimeStamp() < op.timeStamp) {
      this.route.snapshot.data['figureOperation'].timeStamp = op.timeStamp;
      this.route.snapshot.data['figureOperation'].figure = {};
    }
    this.setsOfBoard++;
  }
}
