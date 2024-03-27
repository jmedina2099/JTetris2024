import { Box } from './box';

export interface Figure {
  boxes: Box[];
  initialTimeStamp: number;
  timeStamp: number;
}
