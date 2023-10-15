import { UseFormRegister } from "react-hook-form";
import InputsInterface from "../Interface/InputsInterface";

type ISimpleSelection = {
    register: UseFormRegister<InputsInterface>,
    className: string,
}

export function FullName({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <label htmlFor="input-full-name">Nome Completo: </label>
            <input className="w-full" type="text" id="input-full-name" {...register("full_name",{required: true})} />
        </div>
    )
}

export function Email({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <label htmlFor="input-email">Endereço Eletronico/E-mail: </label>
            <input className="w-full" type="email" id="input-email" {...register("email",{required: true})} />
        </div>
    )
}

export function RG({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <label htmlFor="input-rg">Identidade/RG: </label>
            <input className="w-full" type="text" id="input-rg" {...register("rg",{required: true})} />
        </div>
    )
}

export function CPF({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <label htmlFor="input-cpf">CPF: </label>
            <input className="w-full" type="text" id="input-cpf" {...register("cpf",{required:true})} />
        </div>
    )
}

export function MotherName({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <label htmlFor="input-mother-name">Nome da mãe: </label>
            <input className="w-full" type="text" id="input-mother-name" {...register("mother_name",{required:true})} />
        </div>
    )
}

export function CEP({register,className}:ISimpleSelection){
    return (
        <>
        <div className={className}>
            <label htmlFor="input-cep">CEP (apenas números): </label>
            <input className="w-full" type="text" id="input-cep" {...register("cep",{required:true})} />
        </div>
        <div className={className}>
            <label htmlFor="input-cep-not-found">Ative caso o CEP não seja encontrado: </label>
            <input type="checkbox" id="input-cep-not-found" {...register("cepNotFound")} />
        </div>
        </>
    )
}

export function CTPSn({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <label htmlFor="input-ctps-n">CTPS nº xxx: </label>
            <input className="w-full" type="text" id="input-ctps-n" {...register("ctps_n",{required:true})} />
        </div>
    )
}

export function CTPSserie({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <label htmlFor="input-TPS-serie">Serie xxx: </label>
            <input className="w-full" required type="text" id="input-CTPS-serie" {...register("ctps_serie",{required:true})} />
        </div>
    )
}