// translated from https://github.com/frode-carlsen/scala-workshop/blob/master/scala-workshop/src/main/scala/oppgave6/FP.scala

sealed trait Employee
case class Developer(name: String, salary: Int) extends Employee
case class Manager(name: String, salary: Int, title: String) extends Employee

def salary(employee: Employee): Int = {
  employee match {
    case Developer(_, salary) => salary
    case Manager(_, salary, _) => salary
  }
}

def totalSalary(employees: List[Employee]): Int = {
  employees.map(salary).sum
}

def name(employee: Employee): String = {
  employee match {
    case Developer(name, _) => name
    case Manager(name, _, _) => name
  }
}

def setSalary(salary: Int, employee: Employee): Employee = {
  employee match {
    case Developer(name, _) => Developer(name, salary)
    case Manager(name, _, title) => Manager(name, salary, title)
  }
}

def raiseSalary(increase: Int, employee: Employee): Employee = {
  employee match {
    case Developer(name, salary) => Developer(name, salary + increase)
    case Manager(name, salary, title) => Manager(name, salary + increase, title)
  }
}

def raiseManagerSalaries(increase: Int, employees: List[Employee]): List[Employee] = {
  employees.map(_ match {
    case Manager(name, salary, title) => Manager(name, salary + increase, title)
    case x => x
  })
}

def raiseDeveloperSalaries(increase: Int, employees: List[Employee]): List[Employee] = {
  employees.map(_ match {
    case Developer(name, salary) => Developer(name, salary + increase)
    case x => x
  })
}

def raiseManagerSalariesByPercent(percent: Int, employees: List[Employee]): List[Employee] = {
  employees.map(_ match {
    case Manager(name, salary, title) => Manager(name, (salary * (1 + percent / 100.0)).toInt, title)
    case x => x
  })
}

def raiseDeveloperSalariesByPercent(percent: Int, employees: List[Employee]): List[Employee] = {
  employees.map(_ match {
    case Developer(name, salary) => Developer(name, (salary * (1 + percent / 100.0)).toInt)
    case x => x
  })
}
