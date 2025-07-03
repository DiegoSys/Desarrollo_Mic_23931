import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WizardComponent } from './components/wizard/wizard.component';
import { WizardStepComponent } from './components/wizard-step/wizard-step.component';
import { SelectorTableModule } from './components/selector-table';



@NgModule({
  declarations: [
    WizardComponent,
    WizardStepComponent
  ],
  imports: [
    CommonModule,
    SelectorTableModule
  ],
  exports: [
    WizardComponent,
    WizardStepComponent,
    SelectorTableModule
  ]
})
export class SharedModule { }
