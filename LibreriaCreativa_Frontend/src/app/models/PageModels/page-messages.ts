import { Message } from '../message';

export interface PageMessages {
  content: Message[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}