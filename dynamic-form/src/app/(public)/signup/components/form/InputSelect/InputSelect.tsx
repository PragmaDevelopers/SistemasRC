import { UseFormRegister,UseFormWatch } from "react-hook-form";
import InputsInterface from "../Interface/InputsInterface";
import states from "../../../../../../../api/states/states";
import { useEffect,useState } from "react";

type SelectInterface = {
    register: UseFormRegister<InputsInterface>,
    watch: UseFormWatch<InputsInterface>,
    marginBottom?: number | string
}

type ApiCep = {
    uf: string
}

export function PowerOfAttorney({register,marginBottom}:SelectInterface){
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-power-attorney">Procuração: </label>
            <select required defaultValue="default" id="input-power-attorney" {...register("power_of_attorney")}>
                <option disabled value="default">-- Escolha um tipo de procuração --</option>
                <option value="previdenciario">Previdenciário</option>
                <option value="trabalhista">Trabalhista</option>
                <option value="administrativo">Administrativo</option>
                <option value="civel">Cível</option>
            </select>
        </div>
    )
}

export function Ocuppation({register,marginBottom}:SelectInterface){
    return (
        <div style={{marginBottom: marginBottom}}>
            {/* Futuramente criar um select com várias opções de cargo */}
            <label htmlFor="input-ocuppation">Profissão: </label>
            <input required type="text" id="input-ocuppation" {...register("ocuppation")} />
        </div>
    )
}

export function Nationality({register,marginBottom}:SelectInterface){
    return (
        <div style={{marginBottom: marginBottom}}>
            {/* PRIMEIRA FORMA DE SELECIONAR OS DADOS. MAIS FLEXIVEL */}
            <label htmlFor="input-nationality">Nacionalidade: </label>
            <select required defaultValue="default" id="input-nationality" {...register("nationality")}>
                <option disabled value="default">-- Escolha uma Nacionalidade --</option>
                <option value="brazilian-male">Brasileiro</option>
                <option value="brazilian-female">Brasileira</option>
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
    )
}

export function MaritalStatus({register,marginBottom}:SelectInterface){
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-marital-status">Estado Civil: </label>
            <select required defaultValue="default" id="input-marital-status" {...register("marital_status")}>
                <option disabled value="default">-- Escolha um Estado Civil --</option>
                <option value="single">Solteiro (a)</option>
                <option value="married">Casado (a)</option>
                <option value="divorced">Divorciado (a)</option>
                <option value="widowed">Viúvo (a)</option>
            </select>
        </div>
    )
}

export function UF({register,marginBottom}:SelectInterface){
    //UF é para as informações do documentos
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-UF">UF: </label>
            <select required defaultValue="default" id="input-UF" {...register("uf_id")}>
                <option disabled value="default">-- Escolha um Estado --</option>
                {states.map(state=>{
                    return <option key={state.id} value={state.id}>{state.abbreviation} - {state.name}</option>   
                })}
            </select>
        </div>
    )
}

export function State({register,watch,marginBottom}:SelectInterface){
    //State é para as informações de endereço
    const [dataCep,useDataCep] = useState<ApiCep>();
    useEffect(()=>{
        async function getAddressByCep(){
            const data = await fetch(`https://viacep.com.br/ws/${watch().cep}/json/`);
            const response = await data.json().catch(error=>console.log(error));
            useDataCep(response)
        }
        if(watch().cep){
            if(watch().cep.length === 8){
                getAddressByCep();
            }
        }
    },[watch().cep])
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-state">Estado: </label>
            <select disabled={!watch().cepNotFound} defaultValue="default" required id="input-state" {...register("state_id")}>
                <option disabled value="default">-- Escolha um Estado --</option>
                {states.map(state=>{
                    return <option selected={dataCep && dataCep.uf == state.abbreviation}
                        key={state.id} value={state.id}>{state.abbreviation} - {state.name}</option>   
                })}
            </select>
        </div>
    )
}