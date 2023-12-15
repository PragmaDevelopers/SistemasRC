"use client"
import React, { ReactNode, useState, useEffect, createElement } from "react";
import { IFormSignUpInputs } from "../../../Interface/IFormInputs";
import ICepApi from "../../../Interface/ICepApi";
import { useForm, SubmitHandler } from "react-hook-form";
import { useRouter } from "next/navigation";
import { getAddressManually, tryGetAddressByCep } from "@/utils/handleError";
import {signUp} from "@/utils/inputsValidation";
import { zodResolver } from "@hookform/resolvers/zod";
import { AccordionItem } from "@/components/AccordionItem";
import states from "@/api/states/states";

type IInputType = "text" | "email" | "select" | "radio" | "number" | "checkbox" | "search" | "date" | "tel" | "textarea"

export default function SignUpPageB() {
  const { register, handleSubmit, watch,setValue,formState: {errors} } = useForm<IFormSignUpInputs>({
    resolver: zodResolver(signUp)
  });

  const [accordions,setAccordions] = useState<{pessoa_fisica: any[],pessoa_juridica: any[],}>({
        pessoa_fisica:[
            {
                title: "Cliente",
                inputs: [
                    {
                        type: "search",
                        name: "nome_do_cliente",
                        label: "Nome"
                    },
                    {
                        type: "text",
                        name: "occupation",
                        label: "Profissão"
                    },
                    {
                        type: "select",
                        name: "estado_civil",
                        label: "Estado Civil",
                        children: [
                            {label: "Solteiro (a)",value:"solteiro_(a)"},
                            {label: "Casado (a)",value:"casado_(a)"},
                            {label: "Divorciado (a)",value:"divorciado_(a)"},
                            {label: "Separado (a) de Fato",value:"separado_(a)_de_fato"},
                            {label: "Separado (a) Judicialmente",value:"divorciado_(a)_judicialmente"},
                            {label: "Viúvo (a)",value:"viuvo_(a)"},
                            {label: "União Estável",value:"uniao_estavel"},
                        ]
                    },
                    {
                        type: "text",
                        name: "cpf",
                        label: "CPF"
                    },
                    {
                        type: "select",
                        name: "perfil",
                        label: "Perfil",
                        children: [
                            {label: "Aposentadoria",value:"aposentadoria"},
                            {label: "Aposentadoria/Declaratoria",value:"aposentadoria/declaratoria"},
                            {label: "APOSENTADORIA ESPECIAL",value:"aposentadoria_especial"}
                        ]
                    },
                    {
                        type: "text",
                        name: "nacionalidade",
                        label: "Nacionalidade"
                    },
                    {
                        type: "select",
                        name: "escolaridade",
                        label: "Escolaridade",
                        children: [
                            {label: "E. Fundamental Incompleto",value:"E._fundamental_incompleto"},
                            {label: "E. Fundamental Completo",value:"e._fundamental_completo"},
                            {label: "E. Médio Incompleto",value:"e._medio_incompleto"}
                        ]
                    },
                    {
                        type: "date",
                        name: "nascimento",
                        label: "Nascimento"
                    },
                    {
                        type: "text",
                        name: "rg",
                        label: "RG"
                    },
                    {
                        type: "text",
                        name: "PIS",
                        label: "PIS"
                    },
                ]
            },
            {
                title: "Contato",
                inputs: [
                    {
                        type: "email",
                        name: "email_1",
                        label: "E-mail 1"
                    },
                    {
                        type: "email",
                        name: "email_2",
                        label: "E-mail 2"
                    },
                    {
                        type: "tel",
                        name: "telefone_celular",
                        label: "Telefone Celular"
                    },
                    {
                        type: "tel",
                        name: "telefone_residencial",
                        label: "Telefone Residencial"
                    },
                    {
                        type: "tel",
                        name: "telefone_comercial",
                        label: "Telefone Comercial"
                    },
                    {
                        type: "tel",
                        name: "telefone_fax",
                        label: "Telefone Fax"
                    }
                ]
            },
            {
                title: "Endereço",
                inputs: [
                    {
                        type:"search",
                        name: "cep",
                        label: "CEP"
                    },
                    {
                        type:"select",
                        name: "uf",
                        label: "UF",
                        children: [
                            {label:"RJ",value:"rj"},
                            {label:"AL",value:"al"}
                        ]
                    },
                    {
                        type:"text",
                        name: "endereco",
                        label: "Endereço"
                    },
                    {
                        type:"text",
                        name: "bairro",
                        label: "Bairro"
                    },
                    {
                        type:"text",
                        name: "cidade",
                        label: "Cidade"
                    }
                ]  
            },
            {
                title: "Campos Adicionais",
                inputs: [
                    {
                        type: "text",
                        name: "contato",
                        label: "Contato",
                    },
                    {
                        type: "tel",
                        name: "telefone_contato",
                        label: "Telefone Contato"
                    },
                    {
                        type: "text",
                        name: "nome_do_pai",
                        label: "Nome do pai"
                    },
                    {
                        type: "text",
                        name: "nome_do_mae",
                        label: "Nome da mãe"
                    },
                    {
                        type: "text",
                        name: "c._livre_pf1",
                        label: "C. Livre PF1"
                    },
                    {
                        type: "text",
                        name: "c._livre_pf2",
                        label: "C. Livre PF2",
                        children: [
                            {label:"Não vive em união estável",value:"nao_vive_em_uniao_estavel"},
                            {label:"Vive em união estável",value:"vive_em_uniao_estavel"}
                        ]
                    },
                    {
                        type: "textarea",
                        name: "observacao",
                        label: "Observação"
                    }
                ]
            }      
        ],
        pessoa_juridica:[
            {
                title: "Cliente",
                inputs: [
                    {
                        type: "search",
                        name: "razao_social",
                        label: "Razão Social"
                    },
                    {
                        type: "text",
                        name: "nome_fantasia",
                        label: "Nome Fantasia"
                    },
                    {
                        type: "text",
                        name: "cnpj",
                        label: "CNPJ"
                    },
                    {
                        type: "select",
                        name: "perfil",
                        label: "Perfil",
                        children: [
                            {label: "Aposentadoria",value:"aposentadoria"},
                            {label: "Aposentadoria/Declaratoria",value:"aposentadoria/declaratoria"},
                            {label: "APOSENTADORIA ESPECIAL",value:"aposentadoria_especial"}
                        ]
                    },
                    {
                        type: "text",
                        name: "responsavel",
                        label: "Responsável"
                    },
                    {
                        type: "text",
                        name: "ramo_de_atividade",
                        label: "Ramo de atividade"
                    },
                    {
                        type: "text",
                        name: "inscricao_estadual",
                        label: "Inscrição estadual"
                    },
                    {
                        type: "text",
                        name: "inscricao_municipal",
                        label: "Inscrição municipal"
                    }
                ]
            },
            {
                title: "Contato",
                inputs: [
                    {
                        type: "email",
                        name: "email_1",
                        label: "E-mail 1"
                    },
                    {
                        type: "email",
                        name: "email_2",
                        label: "E-mail 2"
                    },
                    {
                        type: "tel",
                        name: "telefone_1",
                        label: "Telefone 1"
                    },
                    {
                        type: "tel",
                        name: "telefone_2",
                        label: "Telefone 2"
                    },
                    {
                        type: "tel",
                        name: "telefone_fax",
                        label: "Telefone Fax"
                    },
                    {
                        type: "text",
                        name: "site",
                        label: "Site"
                    }
                ]
            },
            {
                title: "Endereço",
                inputs:[
                    {
                        type:"search",
                        name: "cep",
                        label: "CEP"
                    },
                    {
                        type:"select",
                        name: "uf",
                        label: "UF",
                        children: [
                            {label:"RJ",value:"rj"},
                            {label:"AL",value:"al"}
                        ]
                    },
                    {
                        type:"text",
                        name: "endereco",
                        label: "Endereço"
                    },
                    {
                        type:"text",
                        name: "bairro",
                        label: "Bairro"
                    },
                    {
                        type:"text",
                        name: "cidade",
                        label: "Cidade"
                    }
                ]
            },
            {
                title: "Campos Adicionais",
                inputs: [
                    {
                        type: "text",
                        name: "c._livre_pj1",
                        label: "C. Livre PJ1"
                    },
                    {
                        type: "text",
                        name: "c._livre_pj2",
                        label: "C. Livre PJ2",
                        children: [
                            {label:"Não vive em união estável",value:"nao_vive_em_uniao_estavel"},
                            {label:"Vive em união estável",value:"vive_em_uniao_estavel"}
                        ]
                    },
                    {
                        type: "textarea",
                        name: "observacao",
                        label: "Observação"
                    }
                ]
            }
        ]});
        const [inputArr,setInputArr] = useState([{
            label: "",
            valueOrName: ""
        }])
  const [inputCreateModal,setInputCreateModal] = useState<boolean>(false);
  const [inputRemoveModal,setInputRemoveModal] = useState<boolean>(false);
  const [useDraftModal,setUseDraftModal] = useState<boolean>(false);
  const [formSelectInfo,setFormSelectInfo] = useState<{dataType: IInputType,section:string,set:string,drafts:{}[] | null}>({
    section: "Cliente",
    dataType: "text",
    set: "",
    drafts: null
  });
  const [typePerson,setTypePerson] = useState<"pessoa_fisica"|"pessoa_juridica">("pessoa_fisica");

  useEffect(()=>{
    setAccordions(accordions)
  },[typePerson])

  useEffect(()=>{
    const localData = localStorage.getItem("drafts");
    if(localData){
        const drafts = JSON.parse(localData)
        if(drafts){
            setFormSelectInfo({...formSelectInfo,drafts:drafts});
        }
    }
  },[])

  interface ICreateInputOrSection{
    functionType: "new section" | "new input" | "existing input",
    type?:IInputType,
    accordionIndex?:number,
    inputIndex?:number,
    setLabel?:string,
    setName?:string,
    newSectionName?:string,
    valueIndex?:number
  }

  function createInputOrSection({functionType,type,
    accordionIndex,inputIndex,newSectionName,setLabel,setName,valueIndex}:ICreateInputOrSection){
        const newAccordion = accordions[typePerson];
        if(functionType === "existing input" && inputIndex !== undefined && accordionIndex !== undefined && valueIndex !== undefined){
            inputArr.forEach((obj,index)=>{
                if(valueIndex !== -1 && inputIndex !== -1){
                    newAccordion[accordionIndex].inputs[inputIndex as number].children.splice(valueIndex + index,0,{
                        label: obj.label,
                        value: obj.valueOrName
                    })
                }else{
                    newAccordion[accordionIndex].inputs[inputIndex as number].children.push({
                        label: obj.label,
                        value: obj.valueOrName
                    })
                }
            })
        }
        if(type){
            const isChildren = (setLabel && setName);
            if(functionType === "new input" && type && accordionIndex !== undefined && inputIndex !== undefined){
                let usePush = false;
                inputArr.forEach((obj,index,arr)=>{
                    if(isChildren){
                        if(usePush){
                            if(inputIndex === -1){
                                inputIndex = newAccordion[accordionIndex].inputs.length - 1;
                            }
                            newAccordion[accordionIndex].inputs[inputIndex as number].children.push({
                                label: obj.label,
                                value: obj.valueOrName
                            })
                        }else{
                            if(inputIndex !== -1){
                                newAccordion[accordionIndex].inputs.splice(inputIndex,0,{
                                    type: type.replace("new-",""),
                                    name: setName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                    label: setLabel,
                                    children:[{label: obj.label,value: obj.valueOrName}]
                                })
                            }else{
                                newAccordion[accordionIndex].inputs.push({
                                    type: type.replace("new-",""),
                                    name: setName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                    label: setLabel,
                                    children:[{label: obj.label,value: obj.valueOrName}]
                                })
                            }
                            usePush = true;
                        }
                    }else{
                        if(inputIndex !== -1 && inputIndex !== undefined){
                            newAccordion[accordionIndex].inputs.splice(inputIndex + index,0,{
                                type: type.replace("new-",""),
                                name: obj.valueOrName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                label: obj.label
                            })
                        }else{
                            newAccordion[accordionIndex].inputs.push({
                                type: type.replace("new-",""),
                                name: obj.valueOrName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                label: obj.label
                            })
                        }
                    }
                })
            }
            if(functionType === "new section" && accordionIndex !== undefined){
                let usePush = false;
                inputArr.forEach((obj,index)=>{
                    if(isChildren){
                        if(usePush){
                            if(accordionIndex == -1){
                                newAccordion[newAccordion.length - 1].inputs[0].children.push({
                                    label: obj.label,
                                    value: obj.valueOrName
                                })
                            }else{
                                newAccordion[accordionIndex].inputs[0].children.push({
                                    label: obj.label,
                                    value: obj.valueOrName
                                })
                            }
                        }else{
                            if(accordionIndex == -1){
                                newAccordion.push({
                                    title: newSectionName,
                                    inputs: [
                                        {
                                        type: type.replace("new-",""),
                                        name: setName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                        label: setLabel,
                                        children:[{label: obj.label,value: obj.valueOrName}]
                                        }
                                    ]
                                })
                            }else{
                                newAccordion.splice(accordionIndex,0,{
                                    title: newSectionName,
                                    inputs: [
                                        {
                                        type: type.replace("new-",""),
                                        name: setName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                        label: setLabel,
                                        children:[{label: obj.label,value: obj.valueOrName}]
                                        }
                                    ]
                                })
                            }
                            usePush = true;
                        }
                    }else{
                        if(usePush){
                            if(accordionIndex == -1){
                                newAccordion[newAccordion.length - 1].inputs.push({
                                    type: type.replace("new-",""),
                                    name: obj.valueOrName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                    label: obj.label
                                })
                            }else{
                                newAccordion[accordionIndex].inputs.push({
                                    type: type.replace("new-",""),
                                    name: obj.valueOrName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                    label: obj.label
                                })
                            }
                        }else{
                            if(accordionIndex == -1){
                                newAccordion.push({
                                    title: newSectionName,
                                    inputs: [
                                        {
                                        type: type.replace("new-",""),
                                        name: obj.valueOrName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                        label: obj.label
                                        }
                                    ]
                                })
                            }else{
                                newAccordion.splice(accordionIndex,0,{
                                    title: newSectionName,
                                    inputs: [
                                        {
                                        type: type.replace("new-",""),
                                        name: obj.valueOrName.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                                        label: obj.label
                                        }
                                    ]
                                })
                            }
                            
                            usePush = true;
                        }
                    }
                })
            }
        }
        if(typePerson === "pessoa_fisica"){
            setAccordions({pessoa_juridica:accordions.pessoa_juridica,pessoa_fisica:newAccordion});
        }else if(typePerson === "pessoa_juridica"){
            setAccordions({pessoa_fisica:accordions.pessoa_fisica,pessoa_juridica:newAccordion});
        }
  }
  
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
    <div className="mx-auto px-2 w-full max-w-4xl my-3">
        <button className="bg-gray-200 p-2 mb-4 border-b-2 me-3 border-gray-400" onClick={()=>{
            inputCreateModal ? setInputCreateModal(false) : setInputCreateModal(true);
            setInputRemoveModal(false);
            setUseDraftModal(false)
        }} type="button">Criar Input</button>
        <button className="bg-gray-200 p-2 mb-4 border-b-2 me-3 border-gray-400" onClick={()=>{
            inputRemoveModal ? setInputRemoveModal(false) : setInputRemoveModal(true);
            setInputCreateModal(false);
            setUseDraftModal(false)
        }} type="button">Remover Input</button>
        <button className="bg-gray-200 p-2 mb-4 border-b-2 border-gray-400" onClick={()=>{
            useDraftModal ? setUseDraftModal(false) : setUseDraftModal(true);
            setInputCreateModal(false);
            setInputRemoveModal(false);
        }} type="button">Rascunhos</button>
        <div className="flex gap-5">
            {
                inputCreateModal && (
                    <form className="bg-gray-200 w-3/4 p-2 mb-4 border-b-2 border-gray-400 flex flex-col" 
                    onSubmit={(e:any)=>{
                        e.preventDefault();
                        const setLabel = e.target?.set_title?.value;
                        const setName = e.target?.set_identifier?.value;
                        const dataType = e.target.data_type.value;
                        const whichSection = e.target.which_section.value;
                        const whichInputBefore = e.target.which_input_before.value;

                        if(whichSection !== "new-section"){
                            const accordionIndex = accordions[typePerson].findIndex(accordion=>accordion.title === whichSection);
                            if(["radio","select","checkbox"].includes(dataType)){
                                const inputIndex = accordions[typePerson][accordionIndex].inputs.findIndex((input:any)=>{
                                    return input.name === setName;
                                })
                                const valueIndex = accordions[typePerson][accordionIndex].inputs[inputIndex].children?.findIndex((child:any)=>child.value===whichInputBefore);
                                if(valueIndex !== undefined){
                                    createInputOrSection({
                                        functionType: "existing input",
                                        accordionIndex:accordionIndex,
                                        inputIndex: inputIndex,
                                        valueIndex: valueIndex
                                    })
                                }
                            }else{
                                const inputIndex = accordions[typePerson][accordionIndex].inputs.findIndex((input:any)=>{
                                    return input.name === whichInputBefore;
                                })
                                createInputOrSection({
                                    functionType: "new input",
                                    type: dataType,
                                    accordionIndex: accordionIndex,
                                    inputIndex: inputIndex,
                                    setLabel: setLabel,
                                    setName: setName
                                })
                            }
                        }else{
                            const accordionIndex = accordions[typePerson].findIndex((accordion:any)=>{
                                return accordion.title === whichInputBefore;
                            })
                            const newSectionName = e.target.new_section_name.value;
                            createInputOrSection({
                                type:dataType,
                                newSectionName:newSectionName,
                                accordionIndex: accordionIndex,
                                functionType: "new section",
                                setLabel: setLabel,
                                setName:setName
                            })
                        }
                    }}>
                        <div className="mb-3 flex gap-2">
                            <label htmlFor="which-section" className="whitespace-nowrap">Adicionar em qual seção: </label>
                            <select onChange={(e)=>setFormSelectInfo({...formSelectInfo,section:e.target.value})} value={formSelectInfo.section} 
                            className="w-full" name="which_section" id="which-section">
                                {accordions[typePerson].map((accordion,index)=>{
                                    return <option value={accordion.title} key={index}>{accordion.title}</option>
                                })}
                                <option value="new-section">Nova Seção</option>
                            </select>
                        </div>
                        {formSelectInfo.section === "new-section" && 
                            <div className="mb-3 flex gap-2">
                            <label htmlFor="input-section-name" className="whitespace-nowrap">Nome da seção: </label>
                            <input className="w-full" name="new_section_name" id="input-section-name" type="text" />
                        </div>
                        }
                        <div className="mb-3 flex gap-2">
                            <label htmlFor="data-type" className="whitespace-nowrap">Tipo de input: </label>
                            <select onChange={(e)=>setFormSelectInfo({...formSelectInfo,dataType:e.target.value as IInputType})} className="w-full" value={formSelectInfo.dataType} name="data_type" id="data-type">
                                <option value="text">Texto</option>
                                <option value="number">Número</option>
                                <option value="email">E-mail</option>
                                <option value="date">Date</option>
                                {formSelectInfo.section !== "new-section" && <option value="checkbox">Caixa de seleção multipla (existente)</option>}
                                <option value="new-checkbox">Caixa de seleção multipla (novo conjunto)</option>
                                {formSelectInfo.section !== "new-section" && <option value="radio">Caixa de seleção única A (existente)</option>}
                                <option value="new-radio">Caixa de seleção única A (novo conjunto)</option>
                                {formSelectInfo.section !== "new-section" && <option value="select">Caixa de seleção única B (existente)</option>}
                                <option value="new-select">Caixa de seleção única B (novo conjunto)</option>
                            </select>
                        </div> 
                        {
                            ["radio","checkbox","select"].includes(formSelectInfo.dataType) && (
                                <div className="mb-3 flex gap-2">
                                <label className="whitespace-nowrap">Qual é o conjunto? </label>
                                <select onChange={(e)=>{setFormSelectInfo({...formSelectInfo,set:e.target.value})}} className="w-full" name="set_identifier">
                                    {accordions[typePerson].map((accordion)=>{
                                        if(accordion.title === formSelectInfo.section){
                                            return accordion.inputs.map((input:any,index:number)=>{
                                                if(input.type === formSelectInfo.dataType){
                                                    return <option value={input.name} key={index}>{input.label}</option>
                                                }
                                            })
                                        }
                                    })}
                                </select>
                                </div>    
                            )
                        }
                        {
                            ["new-radio","new-checkbox","new-select"].includes(formSelectInfo.dataType) && (
                                <>
                                <div className="mb-3 flex gap-2">
                                    <label htmlFor="set-title" className="whitespace-nowrap">Titulo do conjunto: </label>
                                    <input className="w-full" name="set_title" id="set-title" type="text" />
                                </div>
                                <div className="mb-3 flex gap-2">
                                    <label htmlFor="set-identifier" className="whitespace-nowrap">Identificador do conjunto: </label>
                                    <input className="w-full" name="set_identifier" id="set-identifier" type="text" />
                                </div>
                                </>   
                            )
                        }
                        <div className="border-y-2 border-slate-400 overflow-y-auto pe-2">
                            <div className="max-h-60">
                            {
                                inputArr.map((input,index:number)=>{
                                    return (
                                        <div key={index} className="py-3">
                                            <div className="mb-3 flex gap-2">
                                                <label htmlFor="input-title" className="whitespace-nowrap">Titulo do input {`(${index + 1})`}: </label>
                                                <input onChange={(e)=>{
                                                    const newArr = [...inputArr];
                                                    newArr[index].label = e.target.value;
                                                    setInputArr(newArr);
                                                }} className="w-full" id="input-title" type="text" />
                                            </div>
                                            <div className="flex gap-2">
                                                <label htmlFor="input-identifier" className="whitespace-nowrap">Identificador do input: </label>
                                                <input onChange={(e)=>{
                                                    const newArr = [...inputArr];
                                                    newArr[index].valueOrName = e.target.value;
                                                    setInputArr(newArr);
                                                }} className="w-full" id="input-identifier" type="text" />
                                            </div>
                                        </div>
                                    )
                                })
                            }
                            </div>
                        </div>
                        <div className="my-3 flex gap-2 justify-between">
                            <button className="bg-gray-300 p-2 border-b-2 me-3 border-gray-400" type="button" onClick={()=>{
                                const newArr = [...inputArr];
                                newArr.push({
                                    label: "",
                                    valueOrName: ""
                                })
                                setInputArr(newArr);
                            }}>Adiconar</button>
                            {inputArr.length > 1 && <button className="bg-gray-300 p-2 border-b-2 border-gray-400" type="button" onClick={()=>{
                                const newArr = [...inputArr];
                                newArr.pop()
                                setInputArr(newArr);
                            }}>Remover</button>}
                        </div>
                        <div className="mb-3 flex gap-2">
                            <label htmlFor="which-input-before" className="whitespace-nowrap">Adicionar antes de qual input: </label>
                            <select className="w-full" name="which_input_before" id="which-input-before">
                                {
                                    formSelectInfo.section !== "new-section" ? (
                                        ["radio","select","checkbox"].includes(formSelectInfo.dataType) ? (
                                            accordions[typePerson].map((accordion:any)=>{
                                                if(formSelectInfo.section === accordion.title){
                                                    return accordion.inputs.map((input:any)=>{
                                                        if(input.type === formSelectInfo.dataType && input.name === formSelectInfo.set){
                                                            return input.children?.map((child:any,index:number)=>{
                                                                return <option value={child.value} key={index}>{child.label}</option>
                                                            })
                                                        }
                                                    })
                                                } 
                                            })
                                        ) :
                                        (
                                            accordions[typePerson].map((accordion:any)=>{
                                                if(formSelectInfo.section === accordion.title){
                                                    return accordion.inputs.map((input:any,index:number)=>{
                                                        return <option value={input.name} key={index}>{input.label}</option>
                                                    })
                                                }
                                            })
                                        )
                                    ) : (
                                        accordions[typePerson].map((accordion:any,index:number)=>{
                                            return <option value={accordion.title} key={index}>{accordion.title}</option>
                                        })
                                    )
                                }
                                <option value="final">Final</option>
                            </select>
                        </div>  
                        <button type="submit" className="bg-gray-400 p-2 text-center">Criar</button>
                    </form>
                )
            }
            {
                inputRemoveModal && (
                    <form className="bg-gray-200 w-2/4 p-2 mb-4 border-b-2 border-gray-400 flex flex-col"
                    onSubmit={(e:any)=>{
                        e.preventDefault();
                        const removeSection = e.target.remove_section.value;
                        const removeInput = e.target.remove_input.value;
                        const newAccordions = accordions[typePerson];

                        const accordionIndex = accordions[typePerson].findIndex(accordion=>accordion.title === removeSection);

                        if(removeInput === ""){
                            newAccordions.splice(accordionIndex,1);
                        }else{
                            const inputIndex = newAccordions[accordionIndex].inputs.findIndex((input:any)=>{
                                return input.name === removeInput;
                            })
                            if(inputIndex !== -1){
                                accordions[typePerson][accordionIndex].inputs.splice(inputIndex,1);
                            }
                        }
                        if(typePerson === "pessoa_fisica"){
                            setAccordions({pessoa_juridica:accordions.pessoa_juridica,pessoa_fisica:newAccordions});
                        }else if(typePerson === "pessoa_juridica"){
                            setAccordions({pessoa_fisica:accordions.pessoa_fisica,pessoa_juridica:newAccordions});
                        }
                    }}>
                        <div className="mb-3 flex gap-2">
                            <label htmlFor="remove-section" className="inline-block whitespace-nowrap">Qual seção: </label>
                            <select onChange={(e)=>setFormSelectInfo({...formSelectInfo,section:e.target.value})} value={formSelectInfo.section} 
                            className="w-full" name="remove_section" id="remove-section">
                                {accordions[typePerson].map((accordion:any,index:number)=>{
                                    return <option value={accordion.title} key={index}>{accordion.title}</option>
                                })}
                            </select>
                        </div>
                        <div className="mb-3 flex gap-2">
                        <label htmlFor="remove-input" className="whitespace-nowrap">Qual input: </label>
                        <select className="w-full" name="remove_input" id="remove-input">
                            <option value="">Nenhum</option>
                            {accordions[typePerson].map((accordion:any)=>{
                                if(accordion.title === formSelectInfo.section){
                                    return accordion.inputs.map((input:any,index:number)=>{
                                        return <option value={input.name} key={index}>{input.label}</option>
                                    })
                                }
                            })}
                        </select>
                        </div>    
                        <button type="submit" className="bg-gray-400 p-2 text-center">Remover</button>
                    </form>
                )
            }
            {
                useDraftModal && (
                    <div className="bg-gray-200 w-2/4 p-2 mb-4 border-b-2 border-gray-400 flex flex-col">
                    <form onSubmit={(e:any)=>{
                        e.preventDefault();
                        const selectedDraft = e.target.selected_draft.value;
                        if(selectedDraft !== "new_draft"){
                            const localData = localStorage.getItem("drafts");
                            if(localData){
                                const drafts = JSON.parse(localData)
                                const draftIndex = drafts.findIndex((draft:any)=>draft.name === selectedDraft)
                                if(draftIndex !== -1){
                                    setAccordions(drafts[draftIndex].data);
                                }
                            }
                        }
                    }}>
                        <select className="w-full" name="selected_draft">
                            <option value="new_draft">Novo rascunho</option>
                            {formSelectInfo.drafts && (
                               formSelectInfo.drafts.map((draft:any,index:number)=>{
                                    return <option key={index} value={draft.name}>{draft.name}</option>
                                })
                            )}
                        </select>
                        <button className="bg-gray-400 p-2 text-center block mt-2">Usar rascunho selecionado</button>
                    </form>
                    <form onSubmit={(e:any)=>{
                        e.preventDefault()
                        const draftName = e.target.draft_name.value;
                        let localData = localStorage.getItem("drafts");

                        let drafts:{data:any,name:string,category:string}[] = []

                        if(localData){
                            drafts = JSON.parse(localData);
                        }
                        
                        const draftIndex = drafts.findIndex((draft:any)=>draft.name === draftName)
                        if(draftIndex !== -1){
                            drafts[draftIndex] = {
                                name: draftName,
                                data: accordions,
                                category: "signUp"
                            }
                        }else{
                            drafts.push({
                                name: draftName,
                                category: "signUp",
                                data: accordions
                            })
                        }
                        localStorage.setItem("drafts",JSON.stringify(drafts));
                        setFormSelectInfo({...formSelectInfo,drafts:drafts})
                        alert("Rascunho salvo!")
                    }}>
                        <div className="bg-gray-300 h-1 rounded-md mt-3 mb-2"></div>
                        <label htmlFor="draft-name">Nome do rascunho: </label>
                        <input required className="w-full" id="draft-name" type="text" name="draft_name" />
                        <button type="submit" className="bg-gray-400 p-2 text-center block mt-2">Salvar rascunho atual</button>
                    </form>
                    </div>
                )
            }
        </div>
        <form>
            {accordions[typePerson].map((accordion:any,accordionIndex)=>{
                return <div key={accordionIndex} className="bg-gray-200 p-5 border-b-2 border-gray-400">
                    <AccordionItem className="flex flex-wrap items-center justify-center" title={accordion.title}>
                        {
                            accordion.title === "Cliente" && (
                                <>
                                    <div className="mb-2 w-2/4 border-x-8">
                                        <label className="block">Tipo Pessoa (conjunto fixo)</label>
                                        <div onChange={(e:any)=>{
                                            setTypePerson(e.target.value)
                                        }} className="inline-block">
                                            <input {...register("tipo_pessoa",{required:true})} className="mx-1" type="radio" id="input-tipo-pessoa" value="pessoa_fisica" />
                                            <label htmlFor="input-tipo-pessoa">Pessoa física</label>
                                            <input {...register("tipo_pessoa",{required:true})} className="mx-1" type="radio" id="input-tipo-pessoa" value="pessoa_juridica" />
                                            <label htmlFor="input-tipo-pessoa">Pessoa jurídica</label>
                                        </div>
                                    </div>
                                    <div className="mb-2 w-2/4 border-x-8">
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
                                </>
                            )
                        }
                        {accordion.inputs.map((input:any,inputIndex:number)=>{
                            return (
                                <div className="mb-2 w-2/4 border-x-8" key={inputIndex}>
                                {
                                    ["radio","checkbox"].includes(input.type) && (
                                        <>
                                        <label className="block">{input.label}</label>
                                        {
                                        input.children?.map((child:any,index:number)=>{
                                            return <div onChange={(e:any)=>{
                                                if(input.name === "tipo_pessoa"){
                                                    setTypePerson(e.target.value)
                                                }
                                            }} key={index} className="inline-block">
                                                <input {...register(input.name,{required:true})} className="mx-1" type={input.type} id={"input-"+input.name} value={child.value} />
                                                <label htmlFor={"input-"+input.name}>{child.label}</label>
                                            </div>
                                        })
                                        }
                                        </>
                                    )
                                }
                                {
                                    ["select"].includes(input.type) && (
                                        <>
                                        <label htmlFor={"input-"+input.name} className="block">{input.label}</label>
                                        <select className="w-full" id={"input-"+input.name} name={input.name}>
                                            {input.children?.map((child:any,index:number)=>{
                                                return <option key={index} value={child.value}>{child.label}</option>
                                            })}
                                        </select>
                                        </>
                                    )
                                }
                                {
                                    ["search"].includes(input.type) && (
                                        <>
                                        <label htmlFor={"input-"+input.name} className="block">{input.label}</label>
                                        <input className="w-full" id={"input-"+input.name} type={input.type} name={input.name} />
                                        </>
                                    )
                                }
                                {
                                    ["text","email","number","date","tel"].includes(input.type) && (
                                        <>
                                        <label htmlFor={"input-"+input.name} className="block">{input.label}</label>
                                        <input className="w-full" id={"input-"+input.name} type={input.type} name={input.name} />
                                        </>
                                    )
                                }
                                {
                                    ["textarea"].includes(input.type) && (
                                        <>
                                        <label htmlFor={"input-"+input.name} className="block">{input.label}</label>
                                        <textarea className="w-full" id={"input-"+input.name} name={input.name}></textarea>
                                        </>
                                    )
                                }
                                </div>
                            )
                        })}
                    </AccordionItem>
                </div>;
            })}
            <button className="bg-gray-200 p-2 mt-4 border-b-2 me-3 border-gray-400" type="submit">Enviar</button>
        </form>
    </div>
  );
}