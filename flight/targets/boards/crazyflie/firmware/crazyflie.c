/**
 ******************************************************************************
 * @addtogroup OpenPilotSystem OpenPilot System
 * @brief These files are the core system files of OpenPilot.
 * They are the ground layer just above PiOS. In practice, OpenPilot actually starts
 * in the main() function of openpilot.c
 * @{
 * @addtogroup OpenPilotCore OpenPilot Core
 * @brief This is where the OP firmware starts. Those files also define the compile-time
 * options of the firmware.
 * @{
 * @file       openpilot.c
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2010.
 * @brief      Sets up and runs main OpenPilot tasks.
 * @see        The GNU Public License (GPL) Version 3
 *
 *****************************************************************************/
/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

#include "inc/openpilot.h"
#include <uavobjectsinit.h>
#include <hwsettings.h>

/* Global Variables */

/* Prototype of PIOS_Board_Init() function */
extern void PIOS_Board_Init(void);
extern void Stack_Change(void);

/**
 * OpenPilot Main function:
 *
 * Initialize PiOS<BR>
 * Create the "System" task (SystemModInitializein Modules/System/systemmod.c) <BR>
 * Start FreeRTOS Scheduler (vTaskStartScheduler) (Now handled by caller)
 * If something goes wrong, blink LED1 and LED2 every 100ms
 *
 */
int main()
{
    /* NOTE: Do NOT modify the following start-up sequence */
    /* Any new initialization functions should be added in OpenPilotInit() */

    /* Brings up System using CMSIS functions, enables the LEDs. */
    PIOS_SYS_Init();

    /* swap the stack to use the IRQ stack */
    Stack_Change();

    /* Architecture dependant Hardware and
     * core subsystem initialisation
     * (see pios_board.c for your arch)
     * */
    PIOS_Board_Init();

    /* Initialize modules */
    MODULE_INITIALISE_ALL
    /* Start the FreeRTOS scheduler, which should never return.
     *
     * NOTE: OpenPilot runs an operating system (FreeRTOS), which constantly calls
     * (schedules) function files (modules). These functions never return from their
     * while loops, which explains why each module has a while(1){} segment. Thus,
     * the OpenPilot software actually starts at the vTaskStartScheduler() function,
     * even though this is somewhat obscure.
     *
     * In addition, there are many main() functions in the OpenPilot firmware source tree
     * This is because each main() refers to a separate hardware platform. Of course,
     * C only allows one main(), so only the relevant main() function is compiled when
     * making a specific firmware.
     *
     */
    vTaskStartScheduler();

    /* If all is well we will never reach here as the scheduler will now be running. */

    /* Do some indication to user that something bad just happened */
    while (1) {
#if defined(PIOS_LED_HEARTBEAT)
        PIOS_LED_Toggle(PIOS_LED_HEARTBEAT);
#endif /* PIOS_LED_HEARTBEAT */
        PIOS_DELAY_WaitmS(100);
    }

    return 0;
}

// Clock configuration
void SystemInit(void)
{
    ErrorStatus HSEStartUpStatus;

    RCC_DeInit();
    /* Enable HSE */
    RCC_HSEConfig(RCC_HSE_ON);
    /* Wait till HSE is ready */
    HSEStartUpStatus = RCC_WaitForHSEStartUp();

    if (HSEStartUpStatus == SUCCESS) {
        /* Enable Prefetch Buffer */
        FLASH_PrefetchBufferCmd(FLASH_PrefetchBuffer_Enable);

        /* Flash 2 wait state */
        FLASH_SetLatency(FLASH_Latency_2);

        /* HCLK = SYSCLK */
        RCC_HCLKConfig(RCC_SYSCLK_Div1);

        /* PCLK2 = HCLK */
        RCC_PCLK2Config(RCC_HCLK_Div1);

        /* PCLK1 = HCLK/2 */
        RCC_PCLK1Config(RCC_HCLK_Div2);

        /* ADCCLK = PCLK2/6 = 72 / 6 = 12 MHz*/
        RCC_ADCCLKConfig(RCC_PCLK2_Div6);

        /* PLLCLK = 16MHz/2 * 9 = 72 MHz */
        RCC_PLLConfig(RCC_PLLSource_HSE_Div2, RCC_PLLMul_9);

        /* Enable PLL */
        RCC_PLLCmd(ENABLE);

        /* Wait till PLL is ready */
        while (RCC_GetFlagStatus(RCC_FLAG_PLLRDY) == RESET) {
            ;
        }

        /* Select PLL as system clock source */
        RCC_SYSCLKConfig(RCC_SYSCLKSource_PLLCLK);

        /* Wait till PLL is used as system clock source */
        while (RCC_GetSYSCLKSource() != 0x08) {
            ;
        }
    } else {
        // Cannot start xtal oscillator!
        while (1) {
            ;
        }
    }
}

/**
 * @}
 * @}
 */
