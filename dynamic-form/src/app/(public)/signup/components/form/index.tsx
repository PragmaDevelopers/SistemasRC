"use client"

import { useForm, SubmitHandler } from "react-hook-form";
import states from "../../../../../../api/states/states";
import issuingBody from "../../../../../../api/issuingBody/issuingBody";

type Inputs = {
    fullName: string,
    ocuppation: string,
    nationality: string,
    maritalStatus: string,
    commonLawMarriage: boolean,
    RG: string,
    UF: string,
    issuingBody: string
}

export default function UserForm(){
    const { register, handleSubmit, watch } = useForm<Inputs>();
    const onSumit : SubmitHandler<Inputs> = (data) => console.log(data);
    return (
        <form onSubmit={handleSubmit(onSumit)}>
            <div>
                <label htmlFor="input-full-name">Nome: </label>
                <input required type="text" id="input-full-name" {...register("fullName")} />
            </div>
            <div>
                {/* Futuramente criar um select com várias opções de cargo */}
                <label htmlFor="input-ocuppation">Profissão: </label>
                <input required type="text" id="input-ocuppation" {...register("ocuppation")} />
            </div>
            <div>
                {/* PRIMEIRA FORMA DE SELECIONAR OS DADOS. MAIS FLEXIVEL */}
                <label htmlFor="input-nationality">Nacionalidade: </label>
                <select required id="input-nationality" {...register("nationality")}>
                    <option value="brazilian">Brasileiro (a)</option>
                </select>
                
                {/* 
                    //SEGUNDA FORMA DE SELECIONAR OS DADOS. MENOS FLEXIVEL

                    <p>Nacionalidade: </p>
                    <input required type="radio" value="brazilian-male" id="input-nationality-male" {...register("nationality")} />
                    <label htmlFor="input-nationality-male">Brasileiro</label>
                    <input required type="radio" value="brazilian-female" id="input-nationality-female" {...register("nationality")} />
                    <label htmlFor="input-nationality-female">Brasileira</label> 
                */}
            </div>
            <div>
                <label htmlFor="input-marital-status">Estado Civil: </label>
                <select required id="input-marital-status" {...register("maritalStatus")}>
                    <option value="single">Solteiro (a)</option>
                    <option value="married">Casado (a)</option>
                    <option value="divorced">Divorciado (a)</option>
                    <option value="widowed">Viúvo (a)</option>
                </select>
            </div>
            <div>
                <span>Vive em União Estável: </span>
                <input required type="radio" value="true" id="input-true-common-law-marriage" {...register("commonLawMarriage")} />
                <label htmlFor="input-common-law-marriage">Sim</label>
                <input required type="radio" value="false" id="input-false-common-law-marriage" {...register("commonLawMarriage")} />
                <label htmlFor="input-common-law-marriage">Não</label>
            </div>
            <div>
                <label htmlFor="input-RG">Identidade/RG: </label>
                <input required type="text" id="input-RG" {...register("RG")} />
            </div>
            <div>
                <label htmlFor="input-UF">UF: </label>
                <select required id="input-UF" {...register("UF")}>
                    {states.map(state=>{
                        return <option key={state.id} value={state.abbreviation}>{state.abbreviation} - {state.name}</option>   
                    })}
                </select>
            </div>
            <div>
                <label htmlFor="input-issuing-body">Órgão Emissor: </label>
                <select required id="input-issuing-body" {...register("issuingBody")}>
                    {issuingBody.map(issuing=>{
                        return <option key={issuing.abbreviation+issuing.name} value={issuing.abbreviation}>{issuing.abbreviation} - {issuing.name}</option>   
                    })}
                </select>
            </div>
            <br />
            <button>Enviar</button>
        </form>
    )
}