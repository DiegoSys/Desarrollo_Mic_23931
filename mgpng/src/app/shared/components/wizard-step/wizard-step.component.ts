import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-wizard-step',
  templateUrl: './wizard-step.component.html',
  styleUrls: ['./wizard-step.component.scss']
})
export class WizardStepComponent {
  @Input() title: string = '';
  @Input() index: number = 0;
  @Input() isActive: boolean = false;
  @Input() isCompleted: boolean = false;
  @Output() selected = new EventEmitter<number>();

  selectStep() {
    this.selected.emit(this.index);
  }
}