import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatUiComponent } from './chat-ui.component';

const routes: Routes = [{ path: "", component: ChatUiComponent }];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
  declarations: [ChatUiComponent]
})
export class ChatUiModule { }
