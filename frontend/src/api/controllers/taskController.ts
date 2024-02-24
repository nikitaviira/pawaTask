import type { AxiosResponse } from 'axios';
import apiClient from '@/api/client';

export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export interface SaveTaskDto {
  id: number | null;
  title: string;
  description: string;
  dueDate: string;
  priority: TaskPriority | null;
  comments: string;
}

export interface TaskDto {
  id: number;
  title: string;
  dueDate: string;
}

export default {
  tasks(): Promise<AxiosResponse<TaskDto>> {
    return apiClient().get('/tasks');
  },

  task(taskId: number): Promise<AxiosResponse<SaveTaskDto>> {
    return apiClient().get(`/task/${taskId}`);
  },

  saveTask(task: SaveTaskDto): Promise<AxiosResponse<void>> {
    return apiClient().post('/task/save', task);
  },
};
