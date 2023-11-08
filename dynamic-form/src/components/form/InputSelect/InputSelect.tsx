import { UseFormRegister,UseFormSetValue,UseFormWatch } from "react-hook-form";
import { IFormSignUpAInputs } from "@/Interface/IFormInputs";
import issuingBodies from "@/api/issuingBodies/issuingBodies";
import states from "@/api/states/states";
import { useEffect,useState } from "react";

type ISimpleSelection = {
    register: UseFormRegister<IFormSignUpAInputs>,
    className: string
}

type IIntermediateSelection = {
    register: UseFormRegister<IFormSignUpAInputs>,
    setValue: UseFormSetValue<IFormSignUpAInputs>,
    watch: UseFormWatch<IFormSignUpAInputs>,
    className: string
}

type IAdvancedSelection = {
    register: UseFormRegister<IFormSignUpAInputs>,
    setValue: UseFormSetValue<IFormSignUpAInputs>,
    watch: UseFormWatch<IFormSignUpAInputs>,
    apiInfo: string[]
    className: string
}

export function PowerOfAttorney({register,watch,setValue,className}:IIntermediateSelection){
    const [selectedArr,setSelectedArr] = useState<string[]>([]);
    function addItem(item:string){
        if(!selectedArr.includes(item)){
            setSelectedArr([item,...watch().procuracao as string[]]);
            setValue("procuracao",[item,...selectedArr]);
        }
    }

    function removeItem(item:string){
        const selectedFilter = selectedArr.filter(value=>value !== item);
        setSelectedArr(selectedFilter);
        setValue("procuracao",selectedFilter);
    }

    return (
        <div className={className}>
            <label htmlFor="input-power-attorney">Procuração: </label>
            <select onChange={(e)=>{addItem(e.target.value)}} defaultValue="default" className="w-full" id="input-power-attorney">
                <option disabled value="default">-- Escolha um tipo de procuração --</option>
                <option value="previdenciario">Previdenciário</option>
                <option value="trabalhista">Trabalhista</option>
                <option value="administrativo">Administrativo</option>
                <option value="civel">Cível</option>
            </select>
            <input type="hidden" {...register("procuracao",{required:true})} />
            <div className="flex gap-2 pt-2 flex-wrap">
                {selectedArr.map(value=>{
                    return <span onClick={()=>removeItem(value)} key={value} className="bg-slate-300 inline-block py-1 px-2 cursor-pointer">{value}</span>
                })}
            </div>
        </div>
    )
}

export function Ocuppation({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            {/* Futuramente criar um select com várias opções de cargo */}
            <label htmlFor="input-ocuppation">Profissão: </label>
            <input className="w-full" type="text" id="input-ocuppation" {...register("profissao",{required:true})} />
        </div>
    )
}

export function Nationality({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            {/* PRIMEIRA FORMA DE SELECIONAR OS DADOS. MAIS FLEXIVEL */}
            <label htmlFor="input-nationality">Nacionalidade: </label>
            <select className="w-full" defaultValue="default" id="input-nationality" {...register("nacionalidade",{required:true})}>
                <option disabled value="default">-- Escolha uma Nacionalidade --</option>
                <option value="brasileiro">Brasileiro</option>
                <option value="Brasileira">Brasileira</option>
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

export function MaritalStatus({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <label htmlFor="input-marital-status">Estado Civil: </label>
            <select className="w-full" defaultValue="default" id="input-marital-status" {...register("estado_civil",{required:true})}>
                <option disabled value="default">-- Escolha um Estado Civil --</option>
                <option value="solteiro">Solteiro (a)</option>
                <option value="casado">Casado (a)</option>
                <option value="divorciado">Divorciado (a)</option>
                <option value="viúvo">Viúvo (a)</option>
            </select>
        </div>
    )
}

export function UfForRG({register,className}:ISimpleSelection){
    //UF é para as informações do documentos
    return (
        <div className={className}>
            <label htmlFor="input-UF">UF: </label>
            <select className="w-full" defaultValue="default" id="input-UF" {...register("uf_do_rg",{required:true})}>
                <option disabled value="default">-- Escolha um Estado --</option>
                {states.map(state=>{
                    return <option key={state.id} value={state.abbreviation}>{state.abbreviation} - {state.name}</option>   
                })}
            </select>
        </div>
    )
}

export function IssuingBody({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <label htmlFor="input-issuing-body">Órgão Emissor: </label>
            <select className="w-full" defaultValue="default" id="input-issuing-body" {...register("orgao_emissor",{required:true})}>
                <option disabled value="default">-- Escolha um Órgão Emissor --</option>
                {issuingBodies.map((issuingBody) => {
                return (
                    <option key={issuingBody.id} value={issuingBody.abbreviation}>
                    {issuingBody.abbreviation} - {issuingBody.name}
                    </option>
                );
                })}
            </select>
        </div>
    )
}

export function StateForAddress({register,setValue,watch,apiInfo,className}:IAdvancedSelection){
    //State é para as informações para endereço
    const [state,setState] = useState("");
    useEffect(()=>{
        if(apiInfo?.length === 1){
            setState(apiInfo[0])
            setValue("uf_do_endereco",apiInfo[0])
        }
    },[apiInfo])
    return (
        <div className={className}>
            <label htmlFor="input-state">Estado: </label>
            <input type="hidden" {...register("uf_do_endereco",{required:true})} />
            {!watch().cepNotFound ? 
                <input className="w-full bg-white" onChange={(e)=>{
                    setState(e.target.value)
                    setValue("uf_do_endereco",e.target.value)
                }} value={state} disabled={true} type="text" id="input-state" />
            :
                <select className="w-full" defaultValue="default" required id="input-state" onChange={(e)=>{
                    setState(e.target.value)
                    setValue("uf_do_endereco",e.target.value)
                        }}>
                    <option disabled value="default">-- Escolha um Estado --</option>
                    {states.map(state=>{
                        return <option key={state.id} value={state.abbreviation}>{state.abbreviation} - {state.name}</option>   
                    })}
                    
                </select>
            }
            
        </div>
    )
}

export function City({register,setValue,watch,apiInfo,className}:IAdvancedSelection){
    const [city,setCity] = useState("");
    const [isReset,setIsReset] = useState(false);
    useEffect(()=>{
        if(apiInfo?.length === 1){
            setCity(apiInfo[0]);
            setValue("cidade",apiInfo[0])
        }
        setIsReset(false)
    },[apiInfo])
    return (
        <div className={className}>
            <label htmlFor="input-city">Cidade {watch().cepNotFound && "(Coloque ao menos 3 caracteres)"}: </label>
            <input type="hidden" {...register("cidade",{required:true})} />    
            {(apiInfo?.length <= 1 || isReset) ? 
            <input className="w-full bg-white" onChange={(e)=>{
                setCity(e.target.value)
                setValue("cidade",e.target.value)
            }} value={city} disabled={!watch().cepNotFound} type="text" id="input-city" />
            :
                <>
                    <select className="w-full" required id="input-city" onChange={(e)=>{
                        setCity(e.target.value)
                        setValue("cidade",e.target.value)
                    }}>
                        {apiInfo.map((info)=>{
                            return <option key={info} value={info}>{info}</option>
                        })}
                        
                    </select>
                    <button type="button" onClick={()=>{setIsReset(true)}}
                    >Reset</button>
                </>
            }
        </div>
    )
}

export function Neighborhood({register,setValue,watch,apiInfo,className}:IAdvancedSelection){
    const [neighborhood,setNeighborhood] = useState("");
    const [isReset,setIsReset] = useState(false);
    useEffect(()=>{
        if(apiInfo?.length === 1){
            setNeighborhood(apiInfo[0]);
            setValue("bairro",apiInfo[0]);
        }
        setIsReset(false);
    },[apiInfo])
    return (
        <div className={className}>
            <label htmlFor="input-neighborhood">Bairro {watch().cepNotFound && "(Coloque ao menos 3 caracteres)"}: </label>
            <input type="hidden" {...register("bairro",{required:true})} /> 
            {(apiInfo?.length <= 1 || isReset)? 
                <input className="w-full bg-white" onChange={(e)=>{
                    setNeighborhood(e.target.value)
                    setValue("bairro",e.target.value);
                }} value={neighborhood} disabled={!watch().cepNotFound} type="text" id="input-neighborhood" required />
            : 
            <>
                <select className="w-full" id="input-neighborhood" onChange={(e)=>{
                        setNeighborhood(e.target.value)
                        setValue("bairro",e.target.value);
                    }}>
                    {apiInfo.map((info)=>{
                        return <option key={info} value={info}>{info}</option>
                    })}
                    
                </select>
                <button type="button" onClick={()=>{setIsReset(true)}}
                >Reset</button>
            </>  
            }     
        </div>
    )
}

export function AddressName({register,setValue,watch,apiInfo,className}:IAdvancedSelection){
    const [addressName,setAddressName] = useState("");
    const [isReset,setIsReset] = useState(false);
    useEffect(()=>{
        if(apiInfo?.length === 1){
            setAddressName(apiInfo[0]);
            setValue("logradouro",apiInfo[0]);
        }
        setIsReset(false)
    },[apiInfo])
    return (
        <div className={className}>
            <label htmlFor="input-address-name">Logradouro: </label> 
            <input type="hidden" {...register("logradouro",{required:true})} />
            {apiInfo?.length <= 1 || isReset ? 
            
            <input className="w-full bg-white" onChange={(e)=>{
                setAddressName(e.target.value)
                setValue("logradouro",e.target.value);
            }} value={addressName} disabled={!watch().cepNotFound} type="text" id="input-address-name" />   
            
            :
            <>
                <select className="w-full" id="input-address-name" onChange={(e)=>{
                    setAddressName(e.target.value)
                    setValue("logradouro",e.target.value);
                }}>
                    {apiInfo.map((info)=>{
                        return <option key={info} value={info}>{info}</option>
                    })}
                    
                </select>
                <button type="button" onClick={()=>{setIsReset(true)}}
                >Reset</button>
            </> 
            } 
        </div>
    )
}

// export function AddressType({register,watch,className}:SelectInterface){
//     return (
//         <div className={{className}}>
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

export function UfForCTPS({register,className}:ISimpleSelection){
    //UF é para as informações de CTPS
    return (
        <div className={className}>
            <label htmlFor="input-ctps-uf">UF: </label>
            <select className="w-full" defaultValue="default" id="input-ctps-uf" {...register("uf_do_ctps",{required:true})}>
                <option disabled value="default">-- Escolha um Estado --</option>
                {states.map(state=>{
                    return <option key={state.id} value={state.abbreviation}>{state.abbreviation} - {state.name}</option>   
                })}
            </select>
        </div>
    )
}

