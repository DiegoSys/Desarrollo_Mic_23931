import { Component } from '@angular/core';
import { Message } from 'primeng/api';
import { ProfileService } from '../profile.service';

@Component({
  selector: 'app-profile-user',
  templateUrl: './profile-user.component.html',
  styleUrl: './profile-user.component.scss'
})
export class ProfileUserComponent {

  constructor(public profileService: ProfileService) {}

  messages: Message[] | undefined;



  ngOnInit(): void {
    this.profileService.getUserByUsername()
    this.messages = [{ severity: 'info', detail: 'Recuerda: si deseas actualizar tus datos personales, puedes hacerlo a través del módulo Actualización de datos , que se encuentra dentro de miEspe -> Inicio -> Actualización de datos.' }];
  }

}
