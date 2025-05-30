import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { GenericViewComponent } from './generic-view.component';
import { SpinnerModule } from '../../../spinner/spinner.module';

@NgModule({
  declarations: [GenericViewComponent],
  imports: [
    CommonModule,
    RouterModule,
    ButtonModule,
    SpinnerModule
  ],
  exports: [GenericViewComponent]
})
export class GenericViewModule { }
