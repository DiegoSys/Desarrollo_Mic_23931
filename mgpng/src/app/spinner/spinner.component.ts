import { Component, OnInit } from '@angular/core';
import { SpinnerService } from './spinner.service';

@Component({
  selector: 'app-spinner',
  templateUrl: './spinner.component.html',
  styleUrl: './spinner.component.scss',
})
export class SpinnerComponent implements OnInit {
  isLoading: boolean = false;
  constructor(private spinnerService: SpinnerService) { }

  ngOnInit(): void {
    this.spinnerService.loading$.subscribe((isLoading) => {
      this.isLoading = isLoading;
    });
  }

}
