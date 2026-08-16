import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CategoryListItem, CreateReplyRequest, CreateTopicRequest, Page, ReplyListItem, TopicDetails, TopicListItem } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class ForumsService {
  private readonly http = inject(HttpClient);

  getCategories() {
    return this.http.get<CategoryListItem[]>('/api/forums/categories');
  }

  getCategoryTopics(categoryId: string, page = 0, size = 20) {
    return this.http.get<Page<TopicListItem>>(`/api/forums/categories/${categoryId}/topics?page=${page}&size=${size}`);
  }

  getTopicDetails(topicId: string) {
    return this.http.get<TopicDetails>(`/api/forums/topics/${topicId}`);
  }

  getTopicReplies(topicId: string, page = 0, size = 20) {
    return this.http.get<Page<ReplyListItem>>(`/api/forums/topics/${topicId}/replies?page=${page}&size=${size}`);
  }

  createTopic(data: CreateTopicRequest) {
    return this.http.post<{id: string}>('/api/forums/topics', data);
  }

  createReply(topicId: string, data: { content: string }) {
    return this.http.post<{id: string}>(`/api/forums/topics/${topicId}/replies`, data);
  }
}
