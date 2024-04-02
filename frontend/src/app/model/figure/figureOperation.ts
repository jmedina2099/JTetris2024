import { Figure } from './figure';

export type FigureOperationType =
  | 'NEW_OPERATION'
  | 'MOVEMENT_OPERATION'
  | 'ROTATION_OPERATION'
  | undefined;

export interface FigureOperation {
  figure: Figure;
  initialTimeStamp: number;
  timeStamp: number;
  operation: FigureOperationType;
}
