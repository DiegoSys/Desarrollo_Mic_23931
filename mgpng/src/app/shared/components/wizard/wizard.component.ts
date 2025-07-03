import { Component, ContentChildren, QueryList, AfterContentInit, Output, EventEmitter } from '@angular/core';
import { WizardStepComponent } from '../wizard-step/wizard-step.component';

@Component({
  selector: 'app-wizard',
  templateUrl: './wizard.component.html',
  styleUrls: ['./wizard.component.scss']
})
export class WizardComponent implements AfterContentInit {
  @ContentChildren(WizardStepComponent) steps!: QueryList<WizardStepComponent>;
  activeIndex = 0;

  @Output() stepChange = new EventEmitter<number>(); // <-- Agrega esto
  @Output() completed = new EventEmitter<void>();

  ngAfterContentInit() {
    this.steps.forEach((step, index) => {
      step.index = index;
      step.isActive = index === 0;
      step.selected.subscribe(i => this.goToStep(i));
    });
  }

  goToStep(index: number) {
    if (index >= 0 && index < this.steps.length) {
      this.steps.forEach((step, i) => {
        step.isActive = i === index;
        step.isCompleted = i < index;
      });
      this.activeIndex = index;
      this.stepChange.emit(this.activeIndex); // <-- Emite el índice del paso
    }
  }

  next() {
    this.goToStep(this.activeIndex + 1);
  }

  previous() {
    this.goToStep(this.activeIndex - 1);
  }

  complete() {
    this.completed.emit();
  }
}