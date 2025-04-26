import { Component } from '@angular/core';
import { LayoutService } from './service/app.layout.service';
import packageJson from '../../../package.json';

@Component({
    selector: 'app-footer',
    templateUrl: './app.footer.component.html',
})
export class AppFooterComponent {
    test: Date = new Date();
    version: string = packageJson.version;
    constructor(public layoutService: LayoutService) {}
    public get date(): Date {
        return new Date();
    }
}
