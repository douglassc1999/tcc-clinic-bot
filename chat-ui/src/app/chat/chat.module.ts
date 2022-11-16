import { Route, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatComponent } from './chat.component';
import { ReactiveFormsModule } from '@angular/forms';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';

const routes: Route[] = [{ path: "", component: ChatComponent }];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NzFormModule,
    ReactiveFormsModule,
    NzInputModule,
    NzButtonModule
  ],
  declarations: [ChatComponent]
})
export class ChatModule { }
