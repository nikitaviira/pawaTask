import type { AxiosResponse } from 'axios';
import apiClient from '@/api/client';

export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export const priorityConversion: Record<TaskPriority, string> = {
  [TaskPriority.LOW]: 'Low',
  [TaskPriority.MEDIUM]: 'Medium',
  [TaskPriority.HIGH]: 'High',
  [TaskPriority.CRITICAL]: 'Critical'
};

export enum TaskSortOrder {
  DEFAULT = 'DEFAULT',
  PRIORITY_ASC = 'PRIORITY_ASC',
  PRIORITY_DESC = 'PRIORITY_DESC',
  DUE_DATE_ASC = 'DUE_DATE_ASC',
  DUE_DATE_DESC = 'DUE_DATE_DESC'
}

export interface TaskDto {
  id: number | null;
  title: string;
  description: string;
  priority: TaskPriority | null;
  dueDate: string;
}

export interface TaskDisplayDto {
  id: number;
  title: string;
  priority: TaskPriority;
  dueDate: string;
}

export interface SaveCommentDto {
  taskId?: number;
  comment: string;
}

export interface CommentDto {
  comment: string;
  createdBy: string;
  createdAt: string;
}

export interface TaskDetailsDto {
  id: number;
  title: string;
  description: string;
  createdAt: string;
  createdBy: string;
  lastEditedBy: string;
  dueDate: string;
  priority: TaskPriority;
  comments: CommentDto[];
}

export default {
  tasks(sortOrder: TaskSortOrder): Promise<AxiosResponse<TaskDisplayDto[]>> {
    return apiClient().get('/task/all', { params: { sortOrder } });
  },

  task(taskId: number): Promise<AxiosResponse<TaskDto>> {
    return apiClient().get(`/task/${taskId}`);
  },

  taskDetails(taskId: number): Promise<AxiosResponse<TaskDetailsDto>> {
    return apiClient().get(`/task/${taskId}/details`);
  },

  saveTask(task: TaskDto): Promise<AxiosResponse<void>> {
    return apiClient().post('/task/save', task);
  },

  saveComment(comment: SaveCommentDto): Promise<AxiosResponse<void>> {
    return apiClient().post('/task/comments/save', comment);
  },

  deleteTasks(taskIds: number[]): Promise<AxiosResponse<void>> {
    return apiClient().post('/task/delete', taskIds);
  },
};
