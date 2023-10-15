import { UseFormRegister } from "react-hook-form";
import InputsInterface from "../Interface/InputsInterface";

type ISimpleSelection = {
    register: UseFormRegister<InputsInterface>,
    marginBottom: number | string,
}

export function FullName({register,marginBottom}:ISimpleSelection){
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-full-name">Nome Completo: </label>
            <input type="text" id="input-full-name" {...register("full_name",{required: true})} />
        </div>
    )
}

export function Email({register,marginBottom}:ISimpleSelection){
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-email">Endereço Eletronico/E-mail: </label>
            <input type="email" id="input-email" {...register("email",{required: true})} />
        </div>
    )
}

export function RG({register,marginBottom}:ISimpleSelection){
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-rg">Identidade/RG: </label>
            <input type="text" id="input-rg" {...register("rg",{required: true})} />
        </div>
    )
}

export function CPF({register,marginBottom}:ISimpleSelection){
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-cpf">CPF: </label>
            <input required type="text" id="input-cpf" {...register("cpf")} />
        </div>
    )
}

export function MotherName({register,marginBottom}:ISimpleSelection){
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-mother-name">Nome da mãe: </label>
            <input required type="text" id="input-mother-name" {...register("mother_name")} />
        </div>
    )
}

export function CEP({register,marginBottom}:ISimpleSelection){
    return (
        <>
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-cep">CEP (apenas números): </label>
            <input type="text" id="input-cep" {...register("cep",{required:true})} />
        </div>
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-cep-not-found">Ative caso o CEP não seja encontrado: </label>
            <input type="checkbox" id="input-cep-not-found" {...register("cepNotFound")} />
        </div>
        </>
    )
}

export function CTPSn({register,marginBottom}:ISimpleSelection){
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-ctps-n">CTPS nº xxx: </label>
            <input required type="text" id="input-ctps-n" {...register("ctps_n")} />
        </div>
    )
}

export function CTPSserie({register,marginBottom}:ISimpleSelection){
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-TPS-serie">Serie xxx: </label>
            <input required type="text" id="input-CTPS-serie" {...register("ctps_serie")} />
        </div>
    )
}