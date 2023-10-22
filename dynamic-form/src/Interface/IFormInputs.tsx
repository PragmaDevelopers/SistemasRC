import { TypeOf } from 'zod';
import signUpSchema from '@/utils/inputsValidation';

export type IFormSignUpInputs = TypeOf<typeof signUpSchema>;