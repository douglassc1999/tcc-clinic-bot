/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { ChatBotServiceService } from './chatBotService.service';

describe('Service: ChatBotService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ChatBotServiceService]
    });
  });

  it('should ...', inject([ChatBotServiceService], (service: ChatBotServiceService) => {
    expect(service).toBeTruthy();
  }));
});
