import { UseFormRegister } from "react-hook-form";
import InputsInterface from "../Interface/InputsInterface";

type SelectInterface = {
    register: UseFormRegister<InputsInterface>,
    marginBottom?: number | string
}

export function FullName({register,marginBottom}:SelectInterface){
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-full-name">Nome Completo: </label>
            <input required type="text" id="input-full-name" {...register("full_name")} />
        </div>
    )
}

export function Email({register,marginBottom}:SelectInterface){
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-email">Endereço Eletronico/E-mail: </label>
            <input required type="email" id="input-email" {...register("email")} />
        </div>
    )
}

export function RG({register,marginBottom}:SelectInterface){
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-RG">Identidade/RG: </label>
            <input required type="text" id="input-RG" {...register("rg")} />
        </div>
    )
}