"use client"
import React, { ReactNode, useState, useEffect, createElement } from "react";
import { IFormSignUpBInputs } from "../../../Interface/IFormInputs";
import ICepApi from "../../../Interface/ICepApi";
import { useForm, SubmitHandler } from "react-hook-form";
import { useRouter } from "next/navigation";
import { getAddressManually, tryGetAddressByCep } from "@/utils/handleError";
import {signUpB} from "@/utils/inputsValidation";
import { zodResolver } from "@hookform/resolvers/zod";
import { AccordionItem } from "@/components/AccordionItem";
import states from "@/api/states/states";

type IInputType = "text" | "email" | "select" | "radio" | "number" | "checkbox" | "search" | "date" | "tel" | "textarea"

export default function SignUpPageB() {
  const { register, handleSubmit, watch,setValue,formState: {errors} } = useForm<IFormSignUpBInputs>({
    resolver: zodResolver(signUpB)
  });

  const [accordions,setAccordions] = useState<{pessoa_fisica: any[],pessoa_juridica: any[],}>({
        pessoa_fisica:[
            {
                title: "Cliente",
                inputs: [
                    {
                        type: "radio",
                        name: "tipo_pessoa",
                        label: "Tipo de pessoa",
                        children:[
                        {
                            label: "Pessoa física",
                            value: "pessoa_fisica"
                        },
                        {
                            label: "Pessoa jurídica",
                            value: "pessoa_juridica"
                        }
                        ]
                    },
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
                        type: "radio",
                        name: "tipo_pessoa",
                        label: "Tipo de pessoa",
                        children:[
                        {
                            label: "Pessoa física",
                            value: "pessoa_fisica"
                        },
                        {
                            label: "Pessoa jurídica",
                            value: "pessoa_juridica"
                        }
                        ]
                    },
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

  const [inputCreateModal,setInputCreateModal] = useState<boolean>(false);
  const [inputRemoveModal,setInputRemoveModal] = useState<boolean>(false);
  const [selectedDataType,setSelectedDataType] = useState<IInputType>("text");
  const [selectedSection,setSelectedSection] = useState("Nova Seção");
  const [typePerson,setTypePerson] = useState<"pessoa_fisica"|"pessoa_juridica">("pessoa_fisica");

  useEffect(()=>{
    setAccordions(accordions)
  },[typePerson])

  function createExistingInput(cloneAccordion:any,accordionIndex:number,inputIndex:number,label:string,valueIndex:number){
    if(valueIndex !== -1){
        cloneAccordion[accordionIndex].inputs[inputIndex].children.splice(valueIndex,0,{
            label: label,
            value: label
        })
    }else{
        cloneAccordion[accordionIndex].inputs[inputIndex].children.push({
            label: label,
            value: label
        })
    }
  }

  function createNewInput(type:IInputType,cloneAccordion:any,accordionIndex:number,inputIndex:number,label:string,extra_label:string){
    if(["radio","checkbox","select"].includes(type)){
        cloneAccordion[accordionIndex].inputs.splice(inputIndex,0,{
            type: type,
            name: label.replace(" ","_").toLowerCase(),
            label: label,
            children:[{label: extra_label,value: extra_label}]
        })
    }else{
        if(["new-radio","new-checkbox","new-select"].includes(type)){
            cloneAccordion[accordionIndex].inputs.push({
                type: type.replace("new-",""),
                name: label.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                label: label,
                children:[{label: extra_label,value: extra_label}]
            })
        }else{
            if(inputIndex !== -1){
                cloneAccordion[accordionIndex].inputs.splice(inputIndex,0,({
                    type: type,
                    name: label.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                    label: label
                }))
            }else{
                cloneAccordion[accordionIndex].inputs.push({
                    type: type,
                    name: label.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                    label: label
                })
            }
        }  
    } 
  }

  function createSection(type:IInputType,newSectionName:string,cloneAccordion:any,label:string,extra_label:string){
    if(["new-radio","new-checkbox","new-select"].includes(type)){
        cloneAccordion.push({
            title: newSectionName,
            inputs: [
                {
                type: type.replace("new-",""),
                name: label.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                label: label,
                children:[{label: extra_label,value: extra_label}]
                }
            ]
        })
    }else{
        cloneAccordion.push({
            title: newSectionName,
            inputs: [{
                type: type,
                name: label.trim().replace(/[ ]{1,}/g,"_").toLowerCase(),
                label: label
            }]
        })
    }  
  }

  return (
    <div className="mx-auto px-2 w-full max-w-4xl my-3">
        <button className="bg-gray-200 p-2 mb-4 border-b-2 me-3 border-gray-400" onClick={()=>{
            inputCreateModal ? setInputCreateModal(false) : setInputCreateModal(true);
            setInputRemoveModal(false);
        }} type="button">Criar Input</button>
        <button className="bg-gray-200 p-2 mb-4 border-b-2 border-gray-400" onClick={()=>{
            inputRemoveModal ? setInputRemoveModal(false) : setInputRemoveModal(true);
            setInputCreateModal(false);
        }} type="button">Remover Input</button>
        <div className="flex gap-5">
        {
            inputCreateModal && (
                <form className="bg-gray-200 w-2/4 p-2 mb-4 border-b-2 border-gray-400 flex flex-col" 
                onSubmit={(e:any)=>{
                    e.preventDefault();
                    const extra_label = e.target?.extra_label?.value;
                    const label = e.target.label.value;
                    const dataType = e.target.data_type.value;
                    const whichSection = e.target.which_section.value;
                    const whichInputBefore = e.target.which_input_before.value;
                   
                    const newAccordions = [...accordions[typePerson]];

                    if(whichSection !== "new-section"){
                        const accordionIndex = accordions[typePerson].findIndex(accordion=>accordion.title === whichSection);
                        if(["radio","select","checkbox"].includes(dataType)){
                            const inputIndex = accordions[typePerson][accordionIndex].inputs.findIndex((input:any)=>{
                                return input.name === extra_label;
                            })
                            const valueIndex = newAccordions[accordionIndex].inputs[inputIndex].children?.findIndex((child:any)=>child.value===whichInputBefore);
                            if(valueIndex !== undefined){
                                createExistingInput(newAccordions,accordionIndex,inputIndex,label,valueIndex);
                            }
                        }else{
                            const inputIndex = accordions[typePerson][accordionIndex].inputs.findIndex((input:any)=>{
                                return input.name === whichInputBefore;
                            })
                            createNewInput(dataType,newAccordions,accordionIndex,inputIndex,label,extra_label);
                        }
                    }else{
                        const newSectionName = e.target.new_section_name.value;
                        createSection(dataType,newSectionName,newAccordions,label,extra_label)
                    }

                    if(typePerson === "pessoa_fisica"){
                        setAccordions({pessoa_juridica:accordions.pessoa_juridica,pessoa_fisica:newAccordions});
                    }else if(typePerson === "pessoa_juridica"){
                        setAccordions({pessoa_fisica:accordions.pessoa_fisica,pessoa_juridica:newAccordions});
                    }
                }}>
                    <div className="mb-3 flex gap-2">
                    <label htmlFor="which-section" className="whitespace-nowrap">Adiconar em qual seção: </label>
                    <select onChange={(e)=>setSelectedSection(e.target.value)} value={selectedSection} 
                    className="w-full" name="which_section" id="which-section">
                        {accordions[typePerson].map((accordion,index)=>{
                            return <option value={accordion.title} key={index}>{accordion.title}</option>
                        })}
                        <option value="new-section">Nova Seção</option>
                    </select>
                    </div>
                    {selectedSection === "new-section" && 
                        <div className="mb-3 flex gap-2">
                        <label htmlFor="input-section-name" className="whitespace-nowrap">Nome da seção: </label>
                        <input className="w-full" name="new_section_name" id="input-section-name" type="text" />
                    </div>
                    }
                    <div className="mb-3 flex gap-2">
                        <label htmlFor="data-type" className="whitespace-nowrap">Tipo de input: </label>
                        <select className="w-full" onChange={(e)=>setSelectedDataType(e.target.value as IInputType)} value={selectedDataType} name="data_type" id="data-type">
                            <option value="text">Texto</option>
                            <option value="number">Número</option>
                            <option value="email">E-mail</option>
                            <option value="date">Date</option>
                            {selectedSection !== "new-section" && <option value="checkbox">Caixa de seleção multipla (existente)</option>}
                            <option value="new-checkbox">Caixa de seleção multipla (novo conjunto)</option>
                            {selectedSection !== "new-section" && <option value="radio">Caixa de seleção única A (existente)</option>}
                            <option value="new-radio">Caixa de seleção única A (novo conjunto)</option>
                            {selectedSection !== "new-section" && <option value="selection">Caixa de seleção única B (existente)</option>}
                            <option value="new-selection">Caixa de seleção única B (novo conjunto)</option>
                        </select>
                    </div> 
                    {
                        ["new-radio","new-checkbox"].includes(selectedDataType) && (
                            <div className="mb-3 flex gap-2">
                            <label className="whitespace-nowrap">Novo Label pai: </label>
                            <input className="w-full" name="extra_label" type="text" />
                            </div>    
                        )
                    }
                    {
                        ["radio","checkbox"].includes(selectedDataType) && (
                            <div className="mb-3 flex gap-2">
                            <label className="whitespace-nowrap">Faz parte de qual label pai: </label>
                            <select className="w-full" name="extra_label">
                                {accordions[typePerson].map((accordion)=>{
                                    return accordion.inputs.map((input:any,index:number)=>{
                                        if(input.type === selectedDataType){
                                            return <option value={input.name} key={index}>{input.label}</option>
                                        }
                                    })
                                })}
                            </select>
                            </div>    
                        )
                    }
                    <div className="mb-3 flex gap-2">
                        <label htmlFor="which-input-before" className="whitespace-nowrap">Adicionar antes de qual input: </label>
                        <select className="w-full" name="which_input_before" id="which-input-before">
                            {
                                selectedSection !== "new-section" ? (
                                    ["radio","select","checkbox"].includes(selectedDataType) ? (
                                        accordions[typePerson].map((accordion:any)=>{
                                            //LABEL FILHA
                                            if(selectedSection === accordion.title){
                                                return accordion.inputs.map((input:any)=>{
                                                    if(input.type === selectedDataType){
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
                                            //LABEL
                                            if(selectedSection === accordion.title){
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
                    <div className="mb-3 flex gap-2">
                        <label htmlFor="input-name" className="whitespace-nowrap">Label filha: </label>
                        <input className="w-full" name="label" id="input-name" type="text" />
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
                        <select onChange={(e)=>setSelectedSection(e.target.value)} value={selectedSection} 
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
                            if(accordion.title === selectedSection){
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
        </div>
        <form>
            {accordions[typePerson].map((accordion:any,accordionIndex)=>{
                return <div key={accordionIndex} className="bg-gray-200 p-5 border-b-2 border-gray-400">
                    <AccordionItem className="flex flex-wrap items-center justify-center" title={accordion.title}>
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