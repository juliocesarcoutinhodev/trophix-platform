import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForumTopic } from './forum-topic';

describe('ForumTopic', () => {
  let component: ForumTopic;
  let fixture: ComponentFixture<ForumTopic>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ForumTopic],
    }).compileComponents();

    fixture = TestBed.createComponent(ForumTopic);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
