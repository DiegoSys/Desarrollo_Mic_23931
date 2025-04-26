import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { ProfileRoutingModule } from './profile-routing.module';
import { ProfileUserComponent } from './profile-user/profile-user.component';
import { TabViewModule } from 'primeng/tabview';
import { MessagesModule } from 'primeng/messages';

import { AvatarModule } from 'primeng/avatar';
import { AvatarGroupModule } from 'primeng/avatargroup';

@NgModule({
  declarations: [
    ProfileUserComponent
  ],
  imports: [
    CommonModule,
    ProfileRoutingModule,
    CardModule,
    TabViewModule,
    MessagesModule,
    AvatarModule,
    AvatarGroupModule,
  ]
})
export class ProfileModule { }
