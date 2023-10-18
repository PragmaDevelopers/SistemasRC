import { z, ZodType, TypeOf } from 'zod';
import signUpSchema from '@/utils/inputsValidation';

type InputsInterface = TypeOf<typeof signUpSchema>;

export default InputsInterface;