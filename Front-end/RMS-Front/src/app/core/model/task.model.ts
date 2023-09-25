export interface TaskModel {
  name: string;
  title?: string;
  description?: string;
  completed: boolean;
  subtasks?: Array<TaskModel>;
}
