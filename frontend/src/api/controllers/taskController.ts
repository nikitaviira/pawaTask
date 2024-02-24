export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export interface CreateTaskRequest {
  title: string;
  description: string;
  dueDate: string;
  priority: TaskPriority | undefined;
  comments: string;
}
