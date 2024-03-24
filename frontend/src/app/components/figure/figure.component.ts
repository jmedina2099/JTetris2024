import { Component, Input } from '@angular/core';
import { Box } from 'src/app/model/figure/box';

@Component({
  selector: 'app-figure',
  templateUrl: './figure.component.html',
  styleUrls: ['./figure.component.css'],
})
export class FigureComponent {
  @Input()
  boxes: Box[] = [];
}
