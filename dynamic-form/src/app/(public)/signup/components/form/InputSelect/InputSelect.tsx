import { UseFormRegister,UseFormSetValue,UseFormWatch } from "react-hook-form";
import InputsInterface from "../Interface/InputsInterface";
import issuingBodies from "../../../../../../../api/issuingBody/issuingBody";
import states from "../../../../../../../api/states/states";
import cities from "../../../../../../../api/cities/cities";
import neighborhoods from "../../../../../../../api/neighborhoods/neighborhoods";
import addressTypes from "../../../../../../../api/addressType/addressType";
import { useEffect,useState } from "react";

type ISimpleSelection = {
    register: UseFormRegister<InputsInterface>,
    marginBottom: number | string
}

export function PowerOfAttorney({register,marginBottom}:ISimpleSelection){
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

export function Ocuppation({register,marginBottom}:ISimpleSelection){
    return (
        <div style={{marginBottom: marginBottom}}>
            {/* Futuramente criar um select com várias opções de cargo */}
            <label htmlFor="input-ocuppation">Profissão: </label>
            <input required type="text" id="input-ocuppation" {...register("ocuppation")} />
        </div>
    )
}

export function Nationality({register,marginBottom}:ISimpleSelection){
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

export function MaritalStatus({register,marginBottom}:ISimpleSelection){
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

export function UfForRG({register,marginBottom}:ISimpleSelection){
    //UF é para as informações do documentos
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-UF">UF: </label>
            <select required defaultValue="default" id="input-UF" {...register("uf_for_RG_id")}>
                <option disabled value="default">-- Escolha um Estado --</option>
                {states.map(state=>{
                    return <option key={state.id} value={state.id}>{state.abbreviation} - {state.name}</option>   
                })}
            </select>
        </div>
    )
}

export function IssuingBody({register,marginBottom}:ISimpleSelection){
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

type IAdvancedSelection = {
    register: UseFormRegister<InputsInterface>,
    setValue: UseFormSetValue<InputsInterface>,
    watch: UseFormWatch<InputsInterface>,
    apiInfo: string[]
    marginBottom: number | string
}

export function StateForAddress({register,setValue,watch,apiInfo,marginBottom}:IAdvancedSelection){
    //State é para as informações para endereço
    const [state,setState] = useState("");
    useEffect(()=>{
        if(apiInfo.length === 1){
            setState(apiInfo[0])
            setValue("state_for_address",apiInfo[0])
        }
    },[apiInfo[0]])
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-state">Estado: </label>
            <input onChange={(e)=>{
                setState(e.target.value)
                setValue("state_for_address",e.target.value)
            }} value={state} disabled={!watch().cepNotFound} type="search" id="input-state" required />
            <input type="hidden" {...register("state_for_address")} />
        </div>
    )
}

export function City({register,setValue,watch,apiInfo,marginBottom}:IAdvancedSelection){
    const [city,setCity] = useState("");
    useEffect(()=>{
        if(apiInfo.length === 1){
            setCity(apiInfo[0]);
            setValue("city",apiInfo[0])
        }
    },[apiInfo[0]])
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-city">Cidade: </label>
            <input onChange={(e)=>{
                setCity(e.target.value)
                setValue("city",e.target.value)
            }} value={city} disabled={!watch().cepNotFound} type="search" id="input-city" />
            <input type="hidden" {...register("city")} />    
        </div>
    )
}

export function Neighborhood({register,setValue,watch,apiInfo,marginBottom}:IAdvancedSelection){
    const [neighborhood,setNeighborhood] = useState("");
    useEffect(()=>{
        if(apiInfo.length === 1){
            setNeighborhood(apiInfo[0]);
            setValue("neighborhood",apiInfo[0]);
        }
    },[apiInfo[0]])
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-neighborhood">Bairro: </label>
            <input onChange={(e)=>{
                setNeighborhood(e.target.value)
                setValue("neighborhood",e.target.value);
            }} value={neighborhood} disabled={!watch().cepNotFound} type="search" id="input-neighborhood" />      
            <input type="hidden" value={neighborhood} {...register("neighborhood",{required:true})} /> 
        </div>
    )
}

export function AddressName({register,setValue,watch,apiInfo,marginBottom}:IAdvancedSelection){
    const [addressName,setAddressName] = useState("");
    useEffect(()=>{
        if(apiInfo.length === 1){
            setAddressName(apiInfo[0]);
            setValue("address_name",apiInfo[0]);
        }
    },[apiInfo[0]])
    return (
        <div style={{ marginBottom: marginBottom }}>
            <label htmlFor="input-address-name"> Nome: </label>
            <input onChange={(e)=>{
                setAddressName(e.target.value)
                setValue("address_name",e.target.value);
            }} value={addressName} disabled={!watch().cepNotFound} type="search" id="input-address-name" />      
            <input type="hidden" {...register("address_name")} />
            {apiInfo.length > 1 && (<select>
                <option>adsad</option>
            </select>)} 
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

export function UfForCTPS({register,marginBottom}:ISimpleSelection){
    //UF é para as informações de CTPS
    return (
        <div style={{marginBottom: marginBottom}}>
            <label htmlFor="input-ctps-uf">UF: </label>
            <select defaultValue="default" required id="input-ctps-uf" {...register("uf_for_ctps")}>
                <option disabled value="default">-- Escolha um Estado --</option>
                {states.map(state=>{
                    return <option key={state.id} value={state.id}>{state.abbreviation} - {state.name}</option>   
                })}
            </select>
        </div>
    )
}

