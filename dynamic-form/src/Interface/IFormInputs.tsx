import { TypeOf } from 'zod';
import {signUpA,signUpB} from '@/utils/inputsValidation';

export type IFormSignUpAInputs = TypeOf<typeof signUpA>;

export type IFormSignUpBInputs = TypeOf<typeof signUpB>;