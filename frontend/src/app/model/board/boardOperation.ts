import { Box } from '../figure/box';

export type BoardOperationType =
  | 'BOARD_WITH_ADDED_FIGURE'
  | 'BOARD_WITH_OCURRED_ONE_LINE'
  | 'BOARD_WITH_OCURRED_TWO_LINES'
  | 'BOARD_WITH_OCURRED_THREE_LINES'
  | 'BOARD_WITH_OCURRED_FOUR_LINES'
  | undefined;

export interface BoardOperation {
  boxes?: Box[];
  timeStamp: number;
  operation: BoardOperationType;
}
