export interface User {
  id: string;
  email: string;
  fullName: string;
  role: 'ROLE_STUDENT' | 'ROLE_ADMIN';
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface Chapter {
  id: number;
  title: string;
  slug: string;
  description: string;
  displayOrder: number;
  totalQuestions: number;
}

export interface Category {
  id: number;
  name: string;
  slug: string;
  description: string;
  icon: string;
  chapters: Chapter[];
}

export interface Option {
  id: string;
  optionKey: 'A' | 'B' | 'C' | 'D';
  optionText: string;
}

export interface Question {
  id: string;
  questionText: string;
  difficulty: 'EASY' | 'MEDIUM' | 'HARD';
  options: Option[];
}

export interface CheckAnswerResponse {
  questionId: string;
  selectedOption: string;
  correctOption: string;
  isCorrect: boolean;
  correct: boolean;
  explanation: string;
  currentStreak: number;
}

export interface StreakSummary {
  currentStreak: number;
  longestStreak: number;
  lastActivityDate: string | null;
  todayQuestionsSolved: number;
}

export interface HeatmapDay {
  date: string;
  count: number;
  level: number; // 0, 1, 2, 3, 4
}

export interface MockTestSummary {
  id: string;
  title: string;
  description: string;
  durationMinutes: number;
  totalQuestions: number;
  totalMarks: number;
  negativeMarkingRate: number;
  difficulty: 'EASY' | 'MEDIUM' | 'HARD';
}

export interface SavedAnswerState {
  questionId: string;
  selectedOption: string | null;
  markedForReview: boolean;
  timeSpentSeconds: number;
}

export interface StartTestResponse {
  sessionId: string;
  mockTestId: string;
  title: string;
  durationMinutes: number;
  totalMarks: number;
  negativeMarkingRate: number;
  startTime: string;
  questions: Question[];
  savedAnswers: Record<string, SavedAnswerState>;
}

export interface QuestionReview {
  questionId: string;
  questionText: string;
  difficulty: string;
  selectedOption: string | null;
  correctOption: string;
  isCorrect: boolean;
  correct?: boolean;
  isAttempted: boolean;
  attempted?: boolean;
  explanation: string;
  options: Option[];
}

export interface TestResultResponse {
  sessionId: string;
  testTitle: string;
  startTime: string;
  endTime: string;
  totalQuestions: number;
  attemptedCount: number;
  correctCount: number;
  incorrectCount: number;
  skippedCount: number;
  totalScore: number;
  maxMarks: number;
  accuracyPercentage: number;
  percentileEstimate: number;
  totalTimeTakenSeconds: number;
  reviewList: QuestionReview[];
}

export interface BulkUploadResult {
  totalProcessed: number;
  successfullyAdded: number;
  skippedCount: number;
  errors: string[];
}
