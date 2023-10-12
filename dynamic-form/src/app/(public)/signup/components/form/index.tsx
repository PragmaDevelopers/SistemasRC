"use client"

import { useForm, SubmitHandler } from "react-hook-form";
import InputsInterface from "./InputsInterface";
import states from "../../../../../../api/states/states";
import issuingBody from "../../../../../../api/issuingBody/issuingBody";
import addressType from "../../../../../../api/addressType/addressType";
import { useEffect,useState } from "react";
import { PowerOfAttorney,Ocuppation } from "./InputSelect/InputSelect";
import { FullName } from "./InputText/InputText";

type ApiCep = {
    uf: string
}

export default function UserForm(){
    const { register, handleSubmit, watch } = useForm<InputsInterface>({
        defaultValues: {
            cepNotFound: false
        }
    });
    const onSumit : SubmitHandler<InputsInterface> = (data) => console.log(data);
    const [dataCep,useDataCep] = useState<ApiCep>();
    useEffect(()=>{
        const subscription = watch();
        console.log(subscription)
        async function getAddressByCep(){
            const data = await fetch(`https://viacep.com.br/ws/${watch().cep}/json/`);
            const response = await data.json().catch(error=>console.log(error));
            useDataCep(response)
        }
        console.log(watch().cep )
        if(watch().cep){
            if(watch().cep.length === 8){
                getAddressByCep();
            }
        }
    },[watch()])
    return (
        <form style={{width: "700px",margin: "0 auto"}} onSubmit={handleSubmit(onSumit)}>
            <div>
                <h3>Campos gerais</h3>
                <PowerOfAttorney register={register} />
                <FullName register={register} />
                <Ocuppation register={register} />
                <div style={{marginBottom: 10}}>
                    {/* PRIMEIRA FORMA DE SELECIONAR OS DADOS. MAIS FLEXIVEL */}
                    <label htmlFor="input-nationality">Nacionalidade: </label>
                    <select required id="input-nationality" {...register("nationality")}>
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
                Ocuppation</div>
                <div>
                    <label htmlFor="input-email">Endereço Eletronico/E-mail: </label>
                    <input required type="email" id="input-email" {...register("email")} />
                </div>
            </div>
            <div>
                <h3>Campos de documentos</h3>
                <div style={{marginBottom: 10}}>
                    <label htmlFor="input-marital-status">Estado Civil: </label>
                    <select required defaultValue="default" id="input-marital-status" {...register("marital_status")}>
                        <option disabled value="default">-- Escolha um Estado Civil --</option>
                        <option value="single">Solteiro (a)</option>
                        <option value="married">Casado (a)</option>
                        <option value="divorced">Divorciado (a)</option>
                        <option value="widowed">Viúvo (a)</option>
                    </select>
                </div>
                <div style={{marginBottom: 10}}>
                    <span>Vive em União Estável: </span>
                    <input required type="radio" value="true" id="input-true-common-law-marriage" {...register("common_law_marriage")} />
                    <label htmlFor="input-common-law-marriage">Sim</label>
                    <input required type="radio" value="false" id="input-false-common-law-marriage" {...register("common_law_marriage")} />
                    <label htmlFor="input-common-law-marriage">Não</label>
                </div>
                <div style={{marginBottom: 10}}>
                    <label htmlFor="input-RG">Identidade/RG: </label>
                    <input required type="text" id="input-RG" {...register("rg")} />
                </div>
                <div style={{marginBottom: 10}}>
                    <label htmlFor="input-UF">UF: </label>
                    <select required defaultValue="default" id="input-UF" {...register("uf_id")}>
                        <option disabled value="default">-- Escolha um Estado --</option>
                        {states.map(state=>{
                            return <option key={state.id} value={state.id}>{state.abbreviation} - {state.name}</option>   
                        })}
                    </select>
                </div>
                <div style={{marginBottom: 10}}> 
                    <label htmlFor="input-issuing-body">Órgão Emissor: </label>
                    <select required defaultValue="default" id="input-issuing-body" {...register("issuing_body_id")}>
                        <option disabled value="default">-- Escolha um Órgão Emissor --</option>
                        {issuingBody.map(issuing=>{
                            return <option key={issuing.abbreviation+issuing.name} value={issuing.id}>{issuing.abbreviation} - {issuing.name}</option>   
                        })}
                    </select>
                </div>
                <div style={{marginBottom: 10}}>
                    <label htmlFor="input-cpf">CPF: </label>
                    <input required type="text" id="input-cpf" {...register("cpf")} />
                </div>
                <div>
                    <label htmlFor="input-mother-name">Nome da mãe: </label>
                    <input required type="text" id="input-mother-name" {...register("mother_name")} />
                </div>
            </div>
                {
                    watch().power_of_attorney === "previdenciario" && (
                        <div>
                            <h3>Campos extras</h3>
                            <div style={{marginBottom: 10}}>
                                <label htmlFor="input-cep">CEP: </label>
                                <input required type="text" id="input-cep" {...register("cep")} />
                            </div>
                            <div style={{marginBottom: 10}}>
                                <label htmlFor="input-cep-not-found">Ative caso o CEP não seja encontrado: </label>
                                <input type="checkbox" id="input-cep-not-found" {...register("cepNotFound")} />
                            </div>
                            <div style={{marginBottom: 10}}>
                                <label htmlFor="input-state">Estado: </label>
                                <select disabled={!watch().cepNotFound} defaultValue="default" required id="input-state" {...register("state_id")}>
                                    <option disabled value="default">-- Escolha um Estado --</option>
                                    {states.map(state=>{
                                        return <option selected={dataCep && dataCep.uf == state.abbreviation}
                                         key={state.id} value={state.id}>{state.abbreviation} - {state.name}</option>   
                                    })}
                                </select>
                            </div>
                            <div style={{marginBottom: 10}}>
                                <label htmlFor="input-address-type">Tipo de logradouro: </label>
                                <select disabled={!watch().cepNotFound} defaultValue="default" required id="input-address-type" {...register("address_type_id")}>
                                    <option disabled value="default">-- Escolha o tipo de logradouro --</option>
                                    {addressType.map(address=>{
                                        return <option key={address.id} value={address.id}>{address.type}</option>   
                                    })}
                                </select>
                                <label htmlFor="input-address-name"> Nome: </label>
                                <input required disabled={!watch().cepNotFound} placeholder="Ex: Rua Camaleão Astuto" type="text" id="input-address-name" {...register("address_name")} />
                            </div>
                            <div style={{marginBottom: 10}}>
                                <span>Tipo de Complemento: </span>
                                <input required type="radio" value="number" id="input-address-complement-number" {...register("address_complement_type")} />
                                <label htmlFor="input-address-complement-number">Número</label>
                                <input required type="radio" value="qd-lt" id="input-address-complement-qd-lt" {...register("address_complement_type")} />
                                <label htmlFor="input-address-complement-qd-lt">Qd/Lt </label>
                                {
                                    watch().address_complement_type === "number" 
                                    ? 
                                        <input placeholder="Adicione o número" required type="number" {...register("address_complement")} />
                                    :
                                    watch().address_complement_type === "qd-lt" 
                                    && 
                                        <input placeholder="Adicione a quadra e lote" required type="text" {...register("address_complement")} />

                                }
                            </div>
                            <div>
                                <label htmlFor="input-neighborhood">Bairro: </label>
                                <input required disabled={!watch().cepNotFound} type="text" id="input-neighborhood" {...register("neighborhood")} />
                            </div>
                        </div>
                    )
                }
            {
                watch().power_of_attorney === "trabalhista" && <h1>Trabalhista</h1>
            }
            {
                watch().power_of_attorney === "admnistrativo" && <h1>Admnistrativo</h1>
            }
            {
                watch().power_of_attorney === "civel" && <h1>Cível</h1>
            }
            <br />
            <button>Enviar</button>
        </form>
    )
}