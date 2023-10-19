"use client";
import React, { ReactNode, useState } from "react";
import InputsInterface from "./Interface/InputsInterface";
import CepDataInterface from "./Interface/CepData";
import { useForm, SubmitHandler } from "react-hook-form";
import {
    PowerOfAttorney,
    Ocuppation,
    Nationality,
    MaritalStatus,
    UfForRG,
    IssuingBody,
    StateForAddress,
    City,
    // AddressType,
    Neighborhood,
    UfForCTPS
} from "./InputSelect/InputSelect";
import { FullName, Email, RG, CPF, MotherName, CEP, AddressName, CTPSn, CTPSserie } from "./InputText/InputText";
import { CommonLawMarriage, AddressComplement } from "./InputRadio/InputRadio";

interface AccordionItemProps {
    title: string;
    children: ReactNode;
    isOpen?: boolean; // Adicione a propriedade isOpen para controlar o estado inicial do accordion
}

const AccordionItem: React.FC<AccordionItemProps> = ({ title, children, isOpen = false }) => {
    const [isAccordionOpen, setIsAccordionOpen] = useState(isOpen);

    return (
        <div>
            <button className="w-full h-6" type="button" onClick={() => setIsAccordionOpen(!isAccordionOpen)}>{title}</button>
            <div className={`${(isAccordionOpen ? 'h-fit mt-8' : 'h-0')} overflow-hidden transition-all`}>
                {children}
            </div>
        </div>
    );
};

export default function UserForm() {
    const { register, handleSubmit, watch } = useForm<InputsInterface>({
        defaultValues: {
            cepNotFound: false,
        },
    });

    const onSubmit: SubmitHandler<InputsInterface> = (data) => console.log(data);

    const [cepData, setCepData] = useState<CepDataInterface>();
    React.useEffect(() => {
        async function getAddressByCep() {
            const data = await fetch(`https://viacep.com.br/ws/${watch().cep}/json/`);
            const response = await data.json().catch(error => console.log(error));
            setCepData(response)
        }
        if (watch().cep?.length === 8) {
            getAddressByCep();
        }
    }, [watch().cep])

    async function getAddress() {
        // const data = await fetch(`https://viacep.com.br/ws/${watch().state_for_address_id}/${
        //   watch().city_id}/${watch().neighborhood_id}/json/`);
        // const response = await data.json().catch(error=>console.log(error));
        // setCepData(response)
    }

    return (
        <form className="w-[65%]" onSubmit={handleSubmit(onSubmit)}>
            <div className="bg-neutral-100 p-2 rounded-md my-2 border-neutral-200 border-2">
                <AccordionItem title="Informações Gerais" isOpen={true}>
                    <PowerOfAttorney marginBottom={10} register={register} />
                    <FullName marginBottom={10} register={register} />
                    <Ocuppation marginBottom={10} register={register} />
                    <Nationality marginBottom={10} register={register} />
                    <Email marginBottom={10} register={register} />
                </AccordionItem>
            </div>
            <div className="bg-neutral-100 p-2 rounded-md my-2 border-neutral-200 border-2">
                <AccordionItem title="Documentos">
                    <MaritalStatus marginBottom={10} register={register} />
                    <CommonLawMarriage marginBottom={10} register={register} />
                    <RG marginBottom={10} register={register} />
                    <UfForRG marginBottom={10} register={register} />
                    <IssuingBody marginBottom={10} register={register} />
                    <CPF marginBottom={10} register={register} />
                    <MotherName marginBottom={10} register={register} />
                </AccordionItem>
            </div>
            {
                ["previdenciario", "civel", "administrativo"].includes(watch().power_of_attorney) && (
                    <div className="bg-neutral-100 p-2 rounded-md my-2 border-neutral-200 border-2">
                        <AccordionItem title="Especificações">
                            <CEP marginBottom={10} register={register} />
                            <StateForAddress marginBottom={10} register={register} watch={watch} apiUf={cepData?.uf} />
                            <City marginBottom={10} register={register} watch={watch} apiCity={cepData?.localidade} />
                            <Neighborhood marginBottom={10} register={register} watch={watch} apiNeighborhood={cepData?.bairro} />
                            {/* <AddressType marginBottom={10} register={register} watch={watch} /> */}
                            <AddressName marginBottom={10} register={register} watch={watch} apiAddressName={cepData?.logradouro} />

                            {watch().cepNotFound && (<button type="button" onClick={() => getAddress()}>Buscar Endereço</button>)}

                            {["previdenciario", "civel"].includes(watch().power_of_attorney) && (
                                <AddressComplement marginBottom={10} register={register} watch={watch} />
                            )}
                        </AccordionItem>
                    </div>
                )
            }
            {
                ["trabalhista"].includes(watch().power_of_attorney) && (
                    <div className="bg-neutral-100 p-2 rounded-md my-2 border-neutral-200 border-2">
                        <AccordionItem title="Campos extras">
                            <CTPSn marginBottom={10} register={register} />
                            <CTPSserie marginBottom={10} register={register} />
                            <UfForCTPS marginBottom={10} register={register} />
                        </AccordionItem>
                    </div>
                )
            }
            <br />
            <button>Enviar</button>
        </form>
    );
}
