import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PruebaRoutingModule } from './prueba-routing.module';
import { PruebaComponent } from './prueba/prueba.component';
import { SpinnerModule } from 'src/app/spinner/spinner.module';
import { SpinnerService } from 'src/app/spinner/spinner.service';

@NgModule({
    declarations: [PruebaComponent],
    imports: [CommonModule, PruebaRoutingModule, SpinnerModule],
    providers: [SpinnerService],
})
export class PruebaModule {}
