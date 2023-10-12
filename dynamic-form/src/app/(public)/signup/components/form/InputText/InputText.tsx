import { UseFormRegister } from "react-hook-form";
import InputsInterface from "../InputsInterface";

type SelectInterface = {
    register: UseFormRegister<InputsInterface>,
}

export function FullName({register}:SelectInterface){
    return (
        <div style={{marginBottom: 10}}>
            <label htmlFor="input-full-name">Nome Completo: </label>
            <input required type="text" id="input-full-name" {...register("full_name")} />
        </div>
    )
}