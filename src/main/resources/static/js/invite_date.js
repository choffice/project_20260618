/* ==================================================
   V2 CALENDAR ENGINE
================================================== */

const srpv2State = {

  startDate: null,
  endDate: null,

  // hoverDate: null,

  currentMonth: new Date(),

  thumbnailFile: null,
  bannerFile: null
};

const srpv2Today = new Date();

srpv2Today.setHours(
    0,
    0,
    0,
    0
);

const srpv2MaxDate = new Date();

srpv2MaxDate.setFullYear(
    srpv2Today.getFullYear() + 1
);

srpv2MaxDate.setHours(
    23,
    59,
    59,
    999
);

/* ==================================================
   ELEMENT
================================================== */

const srpv2CalendarGrid =
    document.getElementById(
        "srpv2CalendarGrid"
    );

const srpv2MonthLabel =
    document.getElementById(
        "srpv2MonthLabel"
    );

const srpv2StartDisplay =
    document.getElementById(
        "srpv2StartDateDisplay"
    );

const srpv2EndDisplay =
    document.getElementById(
        "srpv2EndDateDisplay"
    );

const srpv2StartDateTime =
    document.getElementById(
        "srpv2StartDateTime"
    );

const srpv2EndDateTime =
    document.getElementById(
        "srpv2EndDateTime"
    );

const srpv2DateError =
    document.getElementById(
        "srpv2DateError"
    );

const srpv2Step2Status =
    document.getElementById(
        "srpv2Step2Status"
    );

const srpv2Step3 =
    document.getElementById(
        "srpv2Step3"
    );


//

/* ==================================================
   DATE UTIL
================================================== */

function srpv2FormatDate(date){

  const y =
      date.getFullYear();

  const m =
      String(
          date.getMonth()+1
      ).padStart(2,"0");

  const d =
      String(
          date.getDate()
      ).padStart(2,"0");

  return `${y}-${m}-${d}`;
}

function srpv2SameDate(a,b){

  if(!a || !b){
    return false;
  }

  return (
      a.getFullYear() === b.getFullYear() &&
      a.getMonth() === b.getMonth() &&
      a.getDate() === b.getDate()
  );
}

function srpv2DateOnly(date){

  const cloned =
      new Date(date);

  cloned.setHours(
      0,0,0,0
  );

  return cloned;
}

//

/* ==================================================
   RENDER CALENDAR
================================================== */

function srpv2RenderCalendar(){

  srpv2CalendarGrid.innerHTML = "";

  const weekdays = [
    "일","월","화",
    "수","목","금","토"
  ];

  weekdays.forEach(day=>{

    const header =
        document.createElement(
            "div"
        );

    header.className =
        "srpv2-week-header";

    header.textContent = day;

    srpv2CalendarGrid.appendChild(
        header
    );
  });

  const year =
      srpv2State.currentMonth
          .getFullYear();

  const month =
      srpv2State.currentMonth
          .getMonth();

  srpv2MonthLabel.textContent =
      `${year}년 ${month+1}월`;

  const firstDay =
      new Date(
          year,
          month,
          1
      );

  const lastDay =
      new Date(
          year,
          month+1,
          0
      );

  const firstWeekDay =
      firstDay.getDay();

  for(
      let i=0;
      i<firstWeekDay;
      i++
  ){

    const empty =
        document.createElement(
            "div"
        );

    srpv2CalendarGrid.appendChild(
        empty
    );
  }

  for(
      let day=1;
      day<=lastDay.getDate();
      day++
  ){

    const date =
        new Date(
            year,
            month,
            day
        );

    srpv2CreateDayCell(
        date
    );
  }
}

/* ==================================================
   DAY CELL
================================================== */

function srpv2CreateDayCell(date){

  const cell =
      document.createElement(
          "div"
      );

  cell.className =
      "srpv2-day";

  cell.textContent =
      date.getDate();

  const normalized =
      srpv2DateOnly(date);

  if(
      normalized < srpv2Today ||
      normalized > srpv2MaxDate
  ){

    cell.classList.add(
        "srpv2-day-disabled"
    );

    srpv2CalendarGrid
        .appendChild(cell);

    return;
  }

  srpv2ApplyRangeClass(
      cell,
      normalized
  );

  // cell.addEventListener(
  //     "mouseenter",
  //     ()=>{
  //       srpv2HoverDate(
  //           normalized
  //       );
  //     }
  // );
  //
  // cell.addEventListener(
  //     "mouseleave",
  //     ()=>{
  //       srpv2State.hoverDate =
  //           null;
  //
  //       srpv2RenderCalendar();
  //     }
  // );

  cell.addEventListener(
      "click",
      ()=>{
        srpv2SelectDate(
            normalized
        );
      }
  );

  srpv2CalendarGrid
      .appendChild(cell);
}

/* ==================================================
   HOVER RANGE
================================================== */

function srpv2HoverDate(date){

  // if(
  //     !srpv2State.startDate ||
  //     srpv2State.endDate
  // ){
  //   return;
  // }
  //
  // srpv2State.hoverDate =
  //     date;
  //
  // srpv2RenderCalendar();
}

/* ==================================================
   RANGE STYLE
================================================== */

function srpv2ApplyRangeClass(
    cell,
    date
){

  const start =
      srpv2State.startDate;

  const end =
      srpv2State.endDate;

  const hover =
      srpv2State.hoverDate;

  if(
      start &&
      srpv2SameDate(
          date,
          start
      )
  ){
    cell.classList.add(
        "srpv2-day-start"
    );
  }

  if(
      end &&
      srpv2SameDate(
          date,
          end
      )
  ){
    cell.classList.add(
        "srpv2-day-end"
    );
  }

  if(
      start &&
      end
  ){

    if(
        date > start &&
        date < end
    ){

      cell.classList.add(
          "srpv2-day-range"
      );
    }
  }

  // /* hover preview */
  //
  // if(
  //     start &&
  //     !end &&
  //     hover &&
  //     hover > start
  // ){
  //
  //   if(
  //       date > start &&
  //       date < hover
  //   ){
  //
  //     cell.classList.add(
  //         "srpv2-day-range"
  //     );
  //   }
  // }
}

/* ==================================================
   DATE SELECT
================================================== */

function srpv2SelectDate(date){

  console.log(
      "clicked",
      date,
      srpv2State.startDate,
      srpv2State.endDate
  );

  srpv2DateError.textContent = "";

  /*
      처음 선택
  */

  if(
      !srpv2State.startDate
  ){

    srpv2State.startDate =
        new Date(date);

    srpv2State.endDate = null;

    srpv2StartDisplay.value =
        srpv2FormatDate(date);

    srpv2EndDisplay.value =
        "";

    srpv2RenderCalendar();

    return;
  }

  /*
      시작일만 있는 상태
  */

  if(
      srpv2State.startDate &&
      !srpv2State.endDate
  ){

    if(
        date < srpv2State.startDate
    ){

      srpv2DateError.textContent =
          "종료일은 시작일과 같거나 늦어야 합니다.";

      return;
    }

    srpv2State.endDate =
        new Date(date);

    srpv2EndDisplay.value =
        srpv2FormatDate(date);

    srpv2UpdateDateTimeFields();

    srpv2CompleteStep2();

    srpv2RenderCalendar();

    return;
  }

  /*
      둘 다 선택된 상태

      다시 시작
  */

  srpv2State.startDate =
      new Date(date);

  srpv2State.endDate = null;

  srpv2StartDisplay.value =
      srpv2FormatDate(date);

  srpv2EndDisplay.value =
      "";

  srpv2DateError.textContent =
      "";

  const timeSel = document.getElementById("srpv2TimeSelection");
  if (timeSel) timeSel.style.display = "none";

  srpv2RenderCalendar();
}

//

/* ==================================================
   DATETIME LOCAL
================================================== */

function srpv2Get24HourFromSelects(amPmId, hourId, minId) {
   let ampm = document.getElementById(amPmId).value;
   let h = parseInt(document.getElementById(hourId).value, 10);
   let m = document.getElementById(minId).value;
   
   if (ampm === "AM" && h === 12) h = 0;
   else if (ampm === "PM" && h !== 12) h += 12;
   
   return String(h).padStart(2, '0') + ":" + m;
}

function srpv2UpdateDateTimeFields(){

  if(
      !srpv2State.startDate ||
      !srpv2State.endDate
  ){
    return;
  }

  const start =
      srpv2FormatDate(
          srpv2State.startDate
      );

  const end =
      srpv2FormatDate(
          srpv2State.endDate
      );

  const startTimeStr = srpv2Get24HourFromSelects("inviteStartAmPm", "inviteStartHour", "inviteStartMinute");
  const endTimeStr = srpv2Get24HourFromSelects("inviteEndAmPm", "inviteEndHour", "inviteEndMinute");

  srpv2StartDateTime.value =
      `${start}T${startTimeStr}`;

  srpv2EndDateTime.value =
      `${end}T${endTimeStr}`;
}

document.addEventListener("DOMContentLoaded", () => {
    ["inviteStartAmPm", "inviteStartHour", "inviteStartMinute", "inviteEndAmPm", "inviteEndHour", "inviteEndMinute"].forEach(id => {
       const el = document.getElementById(id);
       if (el) {
           el.addEventListener("change", srpv2UpdateDateTimeFields);
       }
    });
});

/* ==================================================
   STEP2 COMPLETE
================================================== */

function srpv2CompleteStep2(){

  srpv2Step2Status.classList.add(
      "srpv2-complete"
  );

  const timeSel = document.getElementById("srpv2TimeSelection");
  if (timeSel) timeSel.style.display = "block";

  setTimeout(()=>{

    srpv2Step3.classList.remove(
        "srpv2-hidden-step"
    );

  },250);
}

/* ==================================================
   RESET RANGE
================================================== */

document
    .getElementById(
        "srpv2ResetRange"
    )
    .addEventListener(
        "click",
        ()=>{

          srpv2State.startDate =
              null;

          srpv2State.endDate =
              null;

          srpv2State.hoverDate =
              null;

          srpv2StartDisplay.value =
              "";

          srpv2EndDisplay.value =
              "";

          srpv2StartDateTime.value =
              "";

          srpv2EndDateTime.value =
              "";

          srpv2DateError.textContent =
              "";

          srpv2Step2Status.classList.remove(
              "srpv2-complete"
          );

          const timeSel = document.getElementById("srpv2TimeSelection");
          if (timeSel) timeSel.style.display = "none";
          document.getElementById("inviteStartAmPm").value = "AM";
          document.getElementById("inviteStartHour").value = "9";
          document.getElementById("inviteStartMinute").value = "00";
          document.getElementById("inviteEndAmPm").value = "AM";
          document.getElementById("inviteEndHour").value = "10";
          document.getElementById("inviteEndMinute").value = "00";

          document
              .getElementById(
                  "srpv2Step3"
              )
              .classList.add(
              "srpv2-hidden-step"
          );

          srpv2RenderCalendar();
        }
    );

/* ==================================================
   MONTH MOVE
================================================== */

const srpv2PrevMonthBtn =
    document.getElementById(
        "srpv2PrevMonth"
    );

const srpv2NextMonthBtn =
    document.getElementById(
        "srpv2NextMonth"
    );

srpv2PrevMonthBtn
    .addEventListener(
        "click",
        ()=>{

          const target =
              new Date(
                  srpv2State.currentMonth
              );

          target.setMonth(
              target.getMonth()-1
          );

          const minMonth =
              new Date(
                  srpv2Today.getFullYear(),
                  srpv2Today.getMonth(),
                  1
              );

          if(
              target < minMonth
          ){
            return;
          }

          srpv2State.currentMonth =
              target;

          srpv2RenderCalendar();
        }
    );

srpv2NextMonthBtn
    .addEventListener(
        "click",
        ()=>{

          const target =
              new Date(
                  srpv2State.currentMonth
              );

          target.setMonth(
              target.getMonth()+1
          );

          const maxMonth =
              new Date(
                  srpv2MaxDate.getFullYear(),
                  srpv2MaxDate.getMonth(),
                  1
              );

          if(
              target > maxMonth
          ){
            return;
          }

          srpv2State.currentMonth =
              target;

          srpv2RenderCalendar();
        }
    );

/* ==================================================
   INIT
================================================== */

(function(){

  const current =
      new Date();

  current.setDate(1);

  srpv2State.currentMonth =
      current;

  srpv2RenderCalendar();

})();


/* ==================================================
   RESET
================================================== */

document
    .getElementById(
        "srpv2ResetButton"
    )
    .addEventListener(
        "click",
        ()=>{

          location.reload();
        }
    );