"use client";
import { InformationCircleIcon } from "@heroicons/react/24/outline";
import { useState } from "react";

export default function Page() {
    const [cadastrarSe, setCadastrarSe] = useState<boolean>(false);
    const switchCadastrarSe = () => setCadastrarSe(!cadastrarSe);
    return (
        <main className="bg-neutral-50 text-neutral-950 flex flex-row justify-center items-center w-screen h-screen transition-all">
            <div className="h-[90%] w-[60%] relative flex justify-center items-center">
                <div className="w-max absolute top-0 z-10">
                    <h1>Bem-Vindo(a)!</h1>
                    <h2>Insira suas credenciais para acessar o sistema.</h2>
                    <div className="flex flex-row justify-start items-center">
                        <InformationCircleIcon className="aspect-square w-6 mr-2" />
                        <h3>Caso não esteja cadastrado, ultilize o botão <span className="bg-neutral-50 p-2 drop-shadow-md rounded-md ml-2">Cadastrar-se</span></h3>
                    </div>
                </div>
                <form className="flex flex-col items-center mb-4 h-48">
                    {cadastrarSe ? (
                        <div className="h-fit bg-neutral-50 drop-shadow-md rounded-md p-2 border-neutral-200 border-[1px]">
                            <div className="flex flex-col">
                                <input type="text" placeholder="Insira seu nome" className="bg-neutral-100 shadow-inner my-1 border-[1px] border-neutral-200 rounded-md p-1" />
                                <input type="email" placeholder="Insira seu email" className="bg-neutral-100 shadow-inner my-1 border-[1px] border-neutral-200 rounded-md p-1" />
                                <input type="text" placeholder="Insira sua nacionalidade" className="bg-neutral-100 shadow-inner my-1 border-[1px] border-neutral-200 rounded-md p-1" />
                                <input type="text" placeholder="Insira seu genero" className="bg-neutral-100 shadow-inner my-1 border-[1px] border-neutral-200 rounded-md p-1" />
                            </div>
                            <div className="flex flex-row justify-between items-center">
                                <input type="text" placeholder="Insira sua senha" className="bg-neutral-100 shadow-inner my-1 border-[1px] border-neutral-200 rounded-md p-1 mr-1" />
                                <input type="text" placeholder="Re-insira sua senha" className="bg-neutral-100 shadow-inner my-1 border-[1px] border-neutral-200 rounded-md p-1 ml-1" />
                            </div>
                        </div>
                    ) : (
                        <div className="w-96 flex flex-col bg-neutral-50 drop-shadow-md rounded-md p-2 border-neutral-200 border-[1px]">
                            <input type="email" placeholder="Insira seu email" className="bg-neutral-100 shadow-inner my-1 border-[1px] border-neutral-200 rounded-md p-1" />
                            <input type="password" placeholder="Insira sua senha" className="bg-neutral-100 shadow-inner my-1 border-[1px] border-neutral-200 rounded-md p-1" />
                        </div>
                    )}
                    <div className="w-96 flex flex-row justify-between items-center mt-4">
                        <button className="border-neutral-200 border-[1px] text-neutral-950 bg-neutral-50 p-2 drop-shadow-md rounded-md ml-2 hover:bg-neutral-100 hover:text-neutral-950 hover:scale-110 transition-all" type="submit">Entrar</button>
                        <button className="border-neutral-200 border-[1px] text-neutral-950 bg-neutral-50 p-2 drop-shadow-md rounded-md ml-2 hover:bg-neutral-100 hover:text-neutral-950 hover:scale-110 transition-all" type="button" onClick={switchCadastrarSe}>{cadastrarSe ? 'Voltar' : 'Cadastrar-se'}</button>
                    </div>
                </form>
            </div>
        </main>
    );
}
