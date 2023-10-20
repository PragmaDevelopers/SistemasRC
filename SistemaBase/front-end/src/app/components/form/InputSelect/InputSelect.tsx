import { UseFormRegister, UseFormWatch } from "react-hook-form";
import InputsInterface from "../Interface/InputsInterface";
import issuingBodies from "../../../../../api/issuingBody/issuingBody";
import states from "../../../../../api/states/states";
import cities from "../../../../../api/cities/cities";
import neighborhoods from "../../../../../api/neighborhoods/neighborhoods";
import addressTypes from "../../../../../api/addressType/addressType";
import { useEffect, useState } from "react";

type SelectInterface = {
    register: UseFormRegister<InputsInterface>,
    watch?: UseFormWatch<InputsInterface>,
    marginBottom?: number | string,
    apiUf?: string,
    apiCity?: string,
    apiNeighborhood?: string
}

export function PowerOfAttorney({ register, marginBottom }: SelectInterface) {
    return (
        <div style={{ marginBottom: marginBottom }}>
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

export function Ocuppation({ register, marginBottom }: SelectInterface) {
    return (
        <div style={{ marginBottom: marginBottom }}>
            {/* Futuramente criar um select com várias opções de cargo */}
            <label htmlFor="input-ocuppation">Profissão: </label>
            <input required type="text" id="input-ocuppation" {...register("ocuppation")} />
        </div>
    )
}

export function Nationality({ register, marginBottom }: SelectInterface) {
    return (
        <div style={{ marginBottom: marginBottom }}>
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

export function MaritalStatus({ register, marginBottom }: SelectInterface) {
    return (
        <div style={{ marginBottom: marginBottom }}>
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

export function UfForRG({ register, marginBottom }: SelectInterface) {
    //UF é para as informações do documentos
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-UF">UF: </label>
            <select required defaultValue="default" id="input-UF" {...register("uf_for_RG_id")}>
                <option disabled value="default">-- Escolha um Estado --</option>
                {states.map(state => {
                    return <option key={state.id} value={state.id}>{state.abbreviation} - {state.name}</option>
                })}
            </select>
        </div>
    )
}

export function IssuingBody({ register, marginBottom }: SelectInterface) {
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-issuing-body">Órgão Emissor: </label>
            <select required defaultValue="default" id="input-issuing-body" {...register("issuing_body_id")}>
                <option disabled value="default">-- Escolha um Órgão Emissor --</option>
                {issuingBodies.map((issuingBody) => {
                    return (
                        <option key={issuingBody.abbreviation + issuingBody.name} value={issuingBody.id}>
                            {issuingBody.abbreviation} - {issuingBody.name}
                        </option>
                    );
                })}
            </select>
        </div>
    )
}

export function StateForAddress({ register, watch, apiUf, marginBottom }: SelectInterface) {
    //State é para as informações para endereço
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-state">Estado: </label>
            <select disabled={watch ? !watch().cepNotFound : false} defaultValue="default" required {...register("state_for_address_id")}>
                <option disabled value="default">-- Escolha um Estado --</option>
                {states.map(state => {
                    return <option selected={apiUf ? apiUf == state.abbreviation : false}
                        key={state.id} value={state.abbreviation}>{state.abbreviation} - {state.name}</option>
                })}
            </select>
        </div>
    )
}

export function City({ register, watch, apiCity, marginBottom }: SelectInterface) {
    const cepNotFound = watch ? !watch().cepNotFound : false;
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-city">Cidade: </label>
            <input value={cepNotFound ? apiCity : watch && watch().city_id} disabled={cepNotFound} type="search" id="input-city" {...register("city_id", { required: true })} />
        </div>
    )
}

export function Neighborhood({ register, watch, apiNeighborhood, marginBottom }: SelectInterface) {
    const cepNotFound = watch ? !watch().cepNotFound : false;
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-neighborhood">Bairro: </label>
            <input value={cepNotFound ? apiNeighborhood : watch && watch().neighborhood_id} disabled={cepNotFound} type="search" id="input-neighborhood" {...register("neighborhood_id", { required: true })} />
        </div>
    )
}

// export function AddressType({register,watch,marginBottom}:SelectInterface){
//     return (
//         <div style={{ marginBottom: marginBottom }}>
//             <label htmlFor="input-address-type">Tipo de logradouro: </label>
//             <select
//             disabled={watch ? !watch().cepNotFound : false}
//             defaultValue="default"
//             required
//             id="input-address-type"
//             {...register("address_type_id")}
//             >
//             <option disabled value="default">-- Escolha o tipo de logradouro --</option>
//             {addressTypes.map((address) => {
//                 return (
//                 <option key={address.id} value={address.id}>
//                     {address.type}
//                 </option>
//                 );
//             })}
//             </select>
//         </div>
//     )
// }

export function UfForCTPS({ register, marginBottom }: SelectInterface) {
    //UF é para as informações de CTPS
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-ctps-uf">UF: </label>
            <select defaultValue="default" required id="input-ctps-uf" {...register("uf_for_ctps")}>
                <option disabled value="default">-- Escolha um Estado --</option>
                {states.map(state => {
                    return <option key={state.id} value={state.id}>{state.abbreviation} - {state.name}</option>
                })}
            </select>
        </div>
    )
}
