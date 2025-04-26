import { Component, OnInit } from '@angular/core';
import { SpinnerService } from 'src/app/spinner/spinner.service';

@Component({
    selector: 'app-prueba',
    templateUrl: './prueba.component.html',
    styleUrl: './prueba.component.scss',
})
export class PruebaComponent implements OnInit {
    //inicialmente los datos tendran que cargarse
    texto = 'Hola Mundo mientras carga el spinner';
    //se asigna el valor del loading$ del spinner a ser consumido en el html
    loading$ = this.spinner.loading$;
    constructor(private spinner: SpinnerService) {
        
    }
    ngOnInit(): void {

        //ejemplo para cargar el spinner
        this.spinner.setLoadingMainContent(true);
        setTimeout(() => {
            this.spinner.setLoadingMainContent(false);
            this.texto = 'Hola Mundo ya me cargo spinner';
        }
        , 3000);
    }
}
