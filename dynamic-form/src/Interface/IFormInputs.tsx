import { TypeOf } from 'zod';
import {signUp} from '@/utils/inputsValidation';

export type IFormSignUpInputs = TypeOf<typeof signUp>;