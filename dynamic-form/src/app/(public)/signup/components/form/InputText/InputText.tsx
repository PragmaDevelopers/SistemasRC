import { UseFormRegister, UseFormSetValue } from "react-hook-form";
import { IFormSignUpInputs } from "../../../../../../Interface/IFormInputs";
import { useState } from "react";

type ISimpleSelection = {
    register: UseFormRegister<IFormSignUpInputs>,
    className: string,
}

type IAdvancedSelection = {
    register: UseFormRegister<IFormSignUpInputs>,
    setValue: UseFormSetValue<IFormSignUpInputs>,
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

export function RG({register,setValue,className}:IAdvancedSelection){
    const [rgValue,setRgValue] = useState("");
    function formattedRg(rg:string){
        rg = rg.replace(/\D/g,""); //Substituí o que não é dígito por "", /g é [Global][1]
        rg = rg.replace(/^(\d{1,2})(\d{3})(\d{3})(\d{1})$/g,"$1.$2.$3-$4");
        setValue("rg",rg);
        setRgValue(rg)
    }
    return (
        <div className={className}>
            <label htmlFor="input-rg">Identidade/RG: </label>
            <input onChange={(e)=>formattedRg(e.target.value)} minLength={12} maxLength={12} value={rgValue} className="w-full" type="text" id="input-rg" />
            <input type="hidden" {...register("rg",{required:true})} />
        </div>
    )
}

export function CPF({register,setValue,className}:IAdvancedSelection){
    const [cpfValue,setCpfValue] = useState("");
    function formattedCpf(cpf:string){
        cpf = cpf.replace(/\D/g,""); //Substituí o que não é dígito por "", /g é [Global][1]
        cpf = cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/g,"$1.$2.$3-$4");
        setValue("cpf",cpf);
        setCpfValue(cpf)
    }
    return (
        <div className={className}>
            <label htmlFor="input-cpf">CPF: </label>
            <input onChange={(e)=>formattedCpf(e.target.value)} minLength={14} maxLength={14} value={cpfValue} className="w-full" type="text" id="input-cpf" />
            <input type="hidden" {...register("cpf",{required:true})} />
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

export function CEP({register,setValue,className}:IAdvancedSelection){
    const [cepValue,setCepValue] = useState("");
    function formattedCep(cep:string){
        cep = cep.replace(/\D/g,""); //Substituí o que não é dígito por "", /g é [Global][1]
        cep = cep.replace(/^(\d{5})(\d{3})$/g,"$1-$2");
        setValue("cep",cep);
        setCepValue(cep)
    }
    return (
        <>
        <div className={className}>
            <label htmlFor="input-cep">CEP (apenas números): </label>
            <input onChange={(e)=>formattedCep(e.target.value)} minLength={9} maxLength={9} value={cepValue} className="w-full" type="text" id="input-cep" />
            <input type="hidden" {...register("cep",{required:true})} />
        </div>
        <div className={className}>
            <label htmlFor="input-cep-not-found">Ative caso o CEP não seja encontrado: </label>
            <input type="checkbox" id="input-cep-not-found" {...register("cepNotFound",{required:true})} />
        </div>
        </>
    )
}

export function CTPSn({register,setValue,className}:IAdvancedSelection){
    const [ctpsValue,setCtpsValue] = useState("");
    function formattedCtps(ctps:string){
        ctps = ctps.replace(/\D/g,""); //Substituí o que não é dígito por "", /g é [Global][1]
        setValue("ctps_n",ctps);
        setCtpsValue(ctps)
    }
    return (
        <div className={className}>
            <label htmlFor="input-ctps-n">CTPS nº: </label>
            <input onChange={(e)=>formattedCtps(e.target.value)} minLength={7} maxLength={7} value={ctpsValue} className="w-full" type="text" id="input-ctps-n" />
            <input type="hidden" {...register("ctps_n",{required:true})} />
        </div>
    )
}

export function CTPSserie({register,setValue,className}:IAdvancedSelection){
    const [ctpsSerieValue,setCtpsSerieValue] = useState("");
    function formattedCtpsSerie(ctpsSerie:string){
        ctpsSerie = ctpsSerie.replace(/\D/g,""); //Substituí o que não é dígito por "", /g é [Global][1]
        setValue("ctps_serie",ctpsSerie);
        setCtpsSerieValue(ctpsSerie)
    }
    return (
        <div className={className}>
            <label htmlFor="input-TPS-serie">Serie xxx: </label>
            <input onChange={(e)=>formattedCtpsSerie(e.target.value)} minLength={4} maxLength={4} value={ctpsSerieValue} className="w-full" type="text" id="input-CTPS-serie" />
            <input type="hidden" {...register("ctps_serie",{required:true})} />
        </div>
    )
}