export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export interface CreateTaskRequestDto {
  title: string;
  description: string;
  dueDate: string;
  priority: TaskPriority | undefined;
  comments: string;
}

export interface TaskDto {
  id: number;
  title: string;
  dueDate: string;
}
