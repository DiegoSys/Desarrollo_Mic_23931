import { NgxSpinnerService } from "ngx-spinner";
import { BehaviorSubject } from "rxjs";
import { Injectable } from "@angular/core";
@Injectable()
export class SpinnerService {
    private loadingSubject = new BehaviorSubject<boolean>(false);
    loading$ = this.loadingSubject.asObservable();

    constructor(private spinnerService:NgxSpinnerService) {}
    /**
     * Muestra el spinner y asigna el valor true al loadingSubject
     */
    show(){
        this.spinnerService.show();
        this.loadingSubject.next(true);
    }

    /**
     * Oculta el spinner y asigna el valor false al loadingSubject
     */
    hide(){
        this.spinnerService.hide();
        this.loadingSubject.next(false);
    }

    /**
     * Muestra u oculta el spinner dependiendo del valor de isloading
     * Toma el valor de isloading y lo asigna al loadingSubject
     * @param isloading 
     */
    setLoadingMainContent(isloading:boolean){
        this.loadingSubject.next(isloading);
        (isloading) ? this.spinnerService.show() : this.spinnerService.hide();
    }
}