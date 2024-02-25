import type { AxiosResponse } from 'axios';
import apiClient from '@/api/client';

export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export enum TaskSortOrder {
  DEFAULT = 'DEFAULT',
  PRIORITY_ASC = 'PRIORITY_ASC',
  PRIORITY_DESC = 'PRIORITY_DESC',
  DUE_DATE_ASC = 'DUE_DATE_ASC',
  DUE_DATE_DESC = 'DUE_DATE_DESC'
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
  priority: TaskPriority;
}

export default {
  tasks(sortOrder: TaskSortOrder): Promise<AxiosResponse<TaskDto[]>> {
    return apiClient().get('/tasks', { params: { sortOrder } });
  },

  task(taskId: number): Promise<AxiosResponse<SaveTaskDto>> {
    return apiClient().get(`/task/${taskId}`);
  },

  saveTask(task: SaveTaskDto): Promise<AxiosResponse<void>> {
    return apiClient().post('/task/save', task);
  },

  deleteTasks(taskIds: number[]): Promise<AxiosResponse<void>> {
    return apiClient().post('/tasks/delete', taskIds);
  },
};
