class FriendBot {
  constructor() {
    this.style = {
      laugh: 0.35,
      soft: 0.2,
      emoji: 0.15
    };

    this.postpositions = ["한테서", "에게서", "으로부터", "한테", "에게", "에서", "으로", "까지", "보다", "처럼", "같이", "하고", "이라", "라서", "이라서", "으로는", "에서는", "에게는", "은", "는", "이", "가", "을", "를", "에", "와", "과", "도", "만", "의", "로"];

    this.priority = {
      complain: ["선생님", "회사", "시험", "과제", "학원", "돈", "친구", "가족", "병원", "야근", "스트레스"],
      happy: ["합격", "성공", "월급", "휴가", "여행", "치킨", "게임", "선물", "데이트"]
    };

    this.phrase = {
      empathy: ["그렇구나", "그랬네", "음", "아", "이해돼", ""],
      negative: ["쉽지 않았겠다", "많이 힘들었겠다", "속상했겠다", "스트레스였겠다"],
      positive: ["좋은 일이다", "기분 좋았겠다", "축하할 만하네", "다행이다"],
      connect: ["", "근데", "그래도", "그런데"],
      ask: ["무슨 일이 있었어?", "왜 그렇게 됐어?", "어떻게 된 거야?", "괜찮아?"]
    };

    this.state = {
      mood: 50,      // 기분   
      energy: 50,    // 활력    
      curiosity: 50  // 말하고 싶은 정도
    };

    this.ignoreWords = ["은", "는", "이", "가", "을", "를", "에", "에서", "으로", "와", "과", "좀", "진짜", "너무", "완전", "오늘", "지금", "그냥", "약간", "하고", "해서", "근데"];
  }

  reply(text = "") {

    if (!text.trim())
      return this.decorate(this.generateIdle());

    const info = this.analyze(text);

    return this.decorate(this.generate(info));
  }

  decorate(text) {
    if (Math.random() < this.style.laugh && !text.includes("ㅋㅋ"))
      text += "ㅋㅋ";

    if (Math.random() < this.style.soft && !/[.!?]$/.test(text))
      text += ".";

    if (Math.random() < this.style.emoji)
      text += this.random([" 🙂", " 😅", " 🤔"]);

    return text;
  }

  random(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
  }

  mirror(words, intent) {
    if (words.length === 0) return "";
    const w = this.pickKeyword(words, intent);

    if (!w)
      return "";
    return this.random([`${w} 때문인가?`, `${w} 얘기하는 거구나.`, `${w}가 제일 큰 이유 같네.`, `${w}가 신경 쓰였겠다.`, `${w} 쪽이 문제였어?`]);
  }

  stripPostposition(word) {
    for (const p of this.postpositions.sort((a, b) => b.length - a.length)) {
      if (word.endsWith(p) && word.length > p.length + 1) {
        return word.slice(0, -p.length);
      }
    }
    return word;
  }

  extractKeywords(text){
    return text.split(/\s+/).map(w => this.stripPostposition(w))
        .map(w => w.replace(/[^\w가-힣]/g, ""))
        .filter(w => w.length >= 2&&
        !this.ignoreWords.includes(w)
        ).filter(w => !/(했다|한다|했다|했어|이다|였다|된다|같다|혼났다|먹었다)$/.test(w)
        );
  }

  pickKeyword(words, intent) {
    const list = this.priority[intent];
    if (list) {
      for (const p of list) {
        if (words.includes(p)) return p;
      }
    }
    const nouns = words.filter(w => {
      return !/(다|했다|한다|였다|이다)$/.test(w);
    });
    if (nouns.length) return this.random(nouns);
    if (words.length) return this.random(words);
    return "";
  }

  analyze(text) {
    const t = text.toLowerCase();
    const info = {
      type: "default", intent: "normal", emotion: 0, keywords: []
    };
    // ---------- type ----------
    if (/[?？]$/.test(text) || /(뭐해|왜|언제|어디|누구|어떻게)/.test(t)) info.type = "question";
    else if (/(안녕|하이|ㅎㅇ|반가)/.test(t)) info.type = "greeting";
    else if (/(배고|허기|밥|먹고싶|치킨|라면)/.test(t)) info.type = "hungry";
    else if (/(피곤|힘들|지친)/.test(t)) info.type = "tired";
    // ---------- intent ----------    if(info.type==="question")        info.intent="ask";
    if (/(힘들|짜증|우울|스트레스)/.test(t)) info.intent = "complain";
    if (/(좋다|행복|성공|합격)/.test(t)) info.intent = "happy";
    // ---------- emotion ----------    if(/좋|행복|웃|합격|성공/.test(t))        info.emotion+=2;
    if (/힘들|우울|망|짜증|스트레스|싫/.test(t)) info.emotion -= 2;
    if (/ㅋㅋ|ㅎㅎ/.test(t)) info.emotion++;
    // ---------- keywords ----------
    info.keywords = this.extractKeywords(text);
    return info;
  }

  generate(info) {
    if (!info.keywords.length) {
      let bricks = [];
      bricks.push(this.pick("empathy"));

      if (info.emotion < 0)
        bricks.push(this.pick("negative"));

      if (info.emotion > 0)
        bricks.push(this.pick("positive"));

      return this.shuffle(bricks).join(" ");
    }
    let bricks = [];
    bricks.push(this.pick("empathy", 0.8));
    if (info.emotion < 0) bricks.push(this.pick("negative"));
    if (info.emotion > 0) bricks.push(this.pick("positive"));
    if (Math.random() < 0.6) bricks.push(this.pick("ask"));
    const mirror = this.mirror(info.keywords, info.intent);
    if (mirror) bricks.push(mirror);

    const combo = this.combineKeywords(info.keywords);
    if (combo && Math.random() < 0.5) bricks.push(combo);

    bricks = bricks.filter(Boolean);
    return this.shuffle(bricks).join(" ");
  }

  pick(type, chance = 1) {
    if (Math.random() > chance) return "";
    return this.random(this.phrase[type]);
  }

  shuffle(arr) {
    const copy = [...arr];
    for (let i = copy.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [copy[i], copy[j]] = [copy[j], copy[i]];
    }
    return copy;
  }

  generateIdle() {
    this.state.mood += Math.random() * 10 - 5;
    this.state.curiosity += Math.random() * 10 - 5;
    this.state.mood = Math.max(0, Math.min(100, this.state.mood));
    this.state.curiosity =
        Math.max(0, Math.min(100, this.state.curiosity));
    let mood = "normal";
    if (this.state.mood >= 70) mood = "happy"; else if (this.state.mood <= 30) mood = "tired"; else if (this.state.curiosity >= 70) mood = "curious";

    switch (mood) {
      case "happy":
        return this.random(["오늘은 기분이 좋네.", "뭔가 나쁘지 않은 날인듯", "좋은 하루 되자요"]);
      case "tired":
        return this.random(["조금 쉬고 싶다.", "오늘은 떠들 힘도 안 난다", "멍 때리는 중"]);
      case "curious":
        return this.random(["갑자기 궁금한 게 생겼어.", "요즘 뭐 하고 지내?", "재밌는 일 없어?"]);
      default:
        return this.random(["심심해..", "조용하네", "음..."]);
    }
  }

  combineKeywords(words) {
    if (words.length < 2) return "";
    const a = words[0];
    const b = words[1];
    return this.random([`${a} 때문에 ${b}까지 신경 쓰였겠네.`, `${a}랑 ${b}가 같이 엮인 이야기네.`, `${a}도 그렇고 ${b}도 그렇고 쉽지 않았겠다.`, `${a}하고 ${b}가 제일 기억에 남는구나.`]);
  }
}

const bot = new FriendBot();
//bot.reply();